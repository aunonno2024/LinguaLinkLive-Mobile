package com.osastudio.lingualinklive.webrtc

import android.content.Context
import android.util.Log
import org.webrtc.*

class WebRTCManager(
    private val context: Context,
    private val listener: WebRTCListener
) {
    private val TAG = "WebRTCManager"

    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private var peerConnection: PeerConnection? = null
    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null
    private var localSurfaceView: SurfaceViewRenderer? = null
    private var remoteSurfaceView: SurfaceViewRenderer? = null
    private var videoCapturer: VideoCapturer? = null
    private var eglBase: EglBase = EglBase.create()

    interface WebRTCListener {
        fun onLocalSdpReady(sdp: SessionDescription)
        fun onIceCandidateReady(candidate: IceCandidate)
        fun onConnected()
        fun onDisconnected()
    }

    init {
        initializePeerConnectionFactory()
    }

    private fun initializePeerConnectionFactory() {
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)

        val videoEncoderFactory = DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true)
        val videoDecoderFactory = DefaultVideoDecoderFactory(eglBase.eglBaseContext)

        peerConnectionFactory = PeerConnectionFactory.builder()
            .setVideoEncoderFactory(videoEncoderFactory)
            .setVideoDecoderFactory(videoDecoderFactory)
            .createPeerConnectionFactory()
    }

    fun initSurfaceViews(local: SurfaceViewRenderer, remote: SurfaceViewRenderer) {
        localSurfaceView = local
        remoteSurfaceView = remote
        local.init(eglBase.eglBaseContext, null)
        remote.init(eglBase.eglBaseContext, null)
        local.setMirror(true)
    }

    fun startLocalStream(withVideo: Boolean) {
        val audioConstraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
        }
        val audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
        localAudioTrack = peerConnectionFactory.createAudioTrack("audio0", audioSource)

        if (withVideo) {
            videoCapturer = createCameraCapturer()
            val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)
            val videoSource = peerConnectionFactory.createVideoSource(false)
            videoCapturer?.initialize(surfaceTextureHelper, context, videoSource.capturerObserver)
            videoCapturer?.startCapture(1280, 720, 30)
            localVideoTrack = peerConnectionFactory.createVideoTrack("video0", videoSource)
            localSurfaceView?.let { localVideoTrack?.addSink(it) }
        }
    }

    private fun createCameraCapturer(): VideoCapturer? {
        val enumerator = Camera2Enumerator(context)
        val deviceNames = enumerator.deviceNames
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                return enumerator.createCapturer(deviceName, null)
            }
        }
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                return enumerator.createCapturer(deviceName, null)
            }
        }
        return null
    }

    fun createPeerConnection() {
        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
            PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer()
        )

        val rtcConfig = PeerConnection.RTCConfiguration(iceServers).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        }

        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
            override fun onIceCandidate(candidate: IceCandidate?) {
                candidate?.let { listener.onIceCandidateReady(it) }
            }

            override fun onAddStream(stream: MediaStream?) {
                stream?.videoTracks?.firstOrNull()?.addSink(remoteSurfaceView)
            }

            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                when (newState) {
                    PeerConnection.PeerConnectionState.CONNECTED -> listener.onConnected()
                    PeerConnection.PeerConnectionState.DISCONNECTED,
                    PeerConnection.PeerConnectionState.FAILED -> listener.onDisconnected()
                    else -> {}
                }
            }

            override fun onSignalingChange(p0: PeerConnection.SignalingState?) {}
            override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {}
            override fun onIceConnectionReceivingChange(p0: Boolean) {}
            override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {}
            override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {}
            override fun onRemoveStream(p0: MediaStream?) {}
            override fun onDataChannel(p0: DataChannel?) {}
            override fun onRenegotiationNeeded() {}
            override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
                p0?.track()?.let { track ->
                    if (track is VideoTrack) track.addSink(remoteSurfaceView)
                }
            }
        })

        val streamId = "LLLSTREAM"
        val mediaStream = peerConnectionFactory.createLocalMediaStream(streamId)
        localAudioTrack?.let { mediaStream.addTrack(it) }
        localVideoTrack?.let { mediaStream.addTrack(it) }
        peerConnection?.addStream(mediaStream)
    }

    fun createOffer() {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }
        peerConnection?.createOffer(object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription?) {
                sdp?.let {
                    peerConnection?.setLocalDescription(object : SdpObserver {
                        override fun onCreateSuccess(p0: SessionDescription?) {}
                        override fun onSetSuccess() { listener.onLocalSdpReady(it) }
                        override fun onCreateFailure(p0: String?) {}
                        override fun onSetFailure(p0: String?) {}
                    }, it)
                }
            }
            override fun onSetSuccess() {}
            override fun onCreateFailure(p0: String?) { Log.e(TAG, "Offer create fail: $p0") }
            override fun onSetFailure(p0: String?) {}
        }, constraints)
    }

    fun createAnswer() {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }
        peerConnection?.createAnswer(object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription?) {
                sdp?.let {
                    peerConnection?.setLocalDescription(object : SdpObserver {
                        override fun onCreateSuccess(p0: SessionDescription?) {}
                        override fun onSetSuccess() { listener.onLocalSdpReady(it) }
                        override fun onCreateFailure(p0: String?) {}
                        override fun onSetFailure(p0: String?) {}
                    }, it)
                }
            }
            override fun onSetSuccess() {}
            override fun onCreateFailure(p0: String?) { Log.e(TAG, "Answer create fail: $p0") }
            override fun onSetFailure(p0: String?) {}
        }, constraints)
    }

    fun setRemoteDescription(sdp: String, type: SessionDescription.Type) {
        val sessionDescription = SessionDescription(type, sdp)
        peerConnection?.setRemoteDescription(object : SdpObserver {
            override fun onCreateSuccess(p0: SessionDescription?) {}
            override fun onSetSuccess() { Log.d(TAG, "Remote SDP set") }
            override fun onCreateFailure(p0: String?) {}
            override fun onSetFailure(p0: String?) { Log.e(TAG, "Remote SDP fail: $p0") }
        }, sessionDescription)
    }

    fun addIceCandidate(candidate: String, sdpMid: String, sdpMLineIndex: Int) {
        peerConnection?.addIceCandidate(IceCandidate(sdpMid, sdpMLineIndex, candidate))
    }

    fun toggleMute(muted: Boolean) {
        localAudioTrack?.setEnabled(!muted)
    }

    fun toggleCamera(enabled: Boolean) {
        localVideoTrack?.setEnabled(enabled)
    }

    fun switchCamera() {
        (videoCapturer as? CameraVideoCapturer)?.switchCamera(null)
    }

    fun dispose() {
        videoCapturer?.stopCapture()
        videoCapturer?.dispose()
        localVideoTrack?.dispose()
        localAudioTrack?.dispose()
        peerConnection?.close()
        peerConnection?.dispose()
        peerConnectionFactory.dispose()
        localSurfaceView?.release()
        remoteSurfaceView?.release()
        eglBase.release()
    }
}