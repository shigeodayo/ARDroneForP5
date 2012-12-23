/*
 *
  Copyright (c) <2011>, <Shigeo Yoshida>
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
The names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.shigeodayo.ardrone.video;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import com.shigeodayo.ardrone.command.CommandManager;
import com.shigeodayo.ardrone.manager.AbstractManager;
import com.shigeodayo.ardrone.utils.ARDroneUtils;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;

public class VideoManager extends AbstractManager {

	private CommandManager manager = null;

	private ImageListener listener = null;

	// using TCP
	private Socket socket = null;

	@Override
	public boolean connect(int port) {
		try {
			socket = new Socket(inetaddr, port);
			socket.setSoTimeout(3000);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	@Override
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void ticklePort(int port) {
		byte[] buf = { 0x01, 0x00, 0x00, 0x00 };
		try {
			OutputStream os = socket.getOutputStream();
			os.write(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public VideoManager(InetAddress inetaddr, CommandManager manager) {
		this.inetaddr = inetaddr;
		this.manager = manager;
	}

	public void setImageListener(ImageListener listener) {
		this.listener = listener;
	}

	public void removeImageListener() {
		listener = null;
	}

	@Override
	public void run() {
		ticklePort(ARDroneUtils.VIDEO_PORT);
		manager.enableVideoData();
		ticklePort(ARDroneUtils.VIDEO_PORT);
		manager.disableAutomaticVideoBitrate();

		decode();
		/*
		 * byte[] buf = new byte[153600]; DatagramPacket packet = new
		 * DatagramPacket(buf, buf.length); BufferedImage image = null; while
		 * (true) { try { ticklePort(ARDroneUtils.VIDEO_PORT);
		 * socket.receive(packet); image = rrfi.readUINT_RGBImage(buf); if
		 * (listener != null) { listener.imageUpdated(image); } } catch
		 * (IOException e) { e.printStackTrace(); } }
		 */
	}

	private void decode() {
		System.out.println("decode");
		// Let's make sure that we can actually convert video pixel formats.
		/*if (!IVideoResampler
				.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION))
			throw new RuntimeException("you must install the GPL version"
					+ " of Xuggler (with IVideoResampler support) for "
					+ "this demo to work");*/

		// Create a Xuggler container object
		IContainer container = IContainer.make();
		// Open up the container
		try {
			if (container.open(socket.getInputStream(), null) < 0)
				throw new IllegalArgumentException("could not open inpustream");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// query how many streams the call to open found
		int numStreams = container.getNumStreams();

		// and iterate through the streams to find the first video stream
		int videoStreamId = -1;
		IStreamCoder videoCoder = null;
		for (int i = 0; i < numStreams; i++) {
			// Find the stream object
			IStream stream = container.getStream(i);
			// Get the pre-configured decoder that can decode this stream;
			IStreamCoder coder = stream.getStreamCoder();

			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				videoStreamId = i;
				videoCoder = coder;
				break;
			}
		}
		if (videoStreamId == -1)
			throw new RuntimeException("could not find video stream");

		/*
		 * Now we have found the video stream in this file. Let's open up our
		 * decoder so it can do work.
		 */
		if (videoCoder.open() < 0)
			throw new RuntimeException(
					"could not open video decoder for container");

		IVideoResampler resampler = null;
		if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
			// if this stream is not in BGR24, we're going to need to
			// convert it. The VideoResampler does that for us.
			resampler = IVideoResampler.make(videoCoder.getWidth(),
					videoCoder.getHeight(), IPixelFormat.Type.BGR24,
					videoCoder.getWidth(), videoCoder.getHeight(),
					videoCoder.getPixelType());
			if (resampler == null)
				throw new RuntimeException(
						"could not create color space resampler.");
		}

		/*
		 * Now, we start walking through the container looking at each packet.
		 */
		IPacket packet = IPacket.make();
		long firstTimestampInStream = Global.NO_PTS;
		long systemClockStartTime = 0;
		while (container.readNextPacket(packet) >= 0) {
			/*
			 * Now we have a packet, let's see if it belongs to our video stream
			 */
			if (packet.getStreamIndex() == videoStreamId) {
				/*
				 * We allocate a new picture to get the data out of Xuggler
				 */
				IVideoPicture picture = IVideoPicture.make(
						videoCoder.getPixelType(), videoCoder.getWidth(),
						videoCoder.getHeight());

				try {
					int offset = 0;
					while (offset < packet.getSize()) {
						// System.out.println("VideoManager.decode(): decode one image");
						/*
						 * Now, we decode the video, checking for any errors.
						 */
						int bytesDecoded = videoCoder.decodeVideo(picture,
								packet, offset);
						if (bytesDecoded < 0)
							throw new RuntimeException(
									"got error decoding video");
						offset += bytesDecoded;

						/*
						 * Some decoders will consume data in a packet, but will
						 * not be able to construct a full video picture yet.
						 * Therefore you should always check if you got a
						 * complete picture from the decoder
						 */
						if (picture.isComplete()) {
							// System.out.println("VideoManager.decode(): image complete");
							IVideoPicture newPic = picture;
							/*
							 * If the resampler is not null, that means we
							 * didn't get the video in BGR24 format and need to
							 * convert it into BGR24 format.
							 */
							if (resampler != null) {
								// we must resample
								newPic = IVideoPicture
										.make(resampler.getOutputPixelFormat(),
												picture.getWidth(),
												picture.getHeight());
								if (resampler.resample(newPic, picture) < 0)
									throw new RuntimeException(
											"could not resample video");
							}
							if (newPic.getPixelType() != IPixelFormat.Type.BGR24)
								throw new RuntimeException(
										"could not decode video as BGR 24 bit data");

							/**
							 * We could just display the images as quickly as we
							 * decode them, but it turns out we can decode a lot
							 * faster than you think.
							 * 
							 * So instead, the following code does a poor-man's
							 * version of trying to match up the frame-rate
							 * requested for each IVideoPicture with the system
							 * clock time on your computer.
							 * 
							 * Remember that all Xuggler IAudioSamples and
							 * IVideoPicture objects always give timestamps in
							 * Microseconds, relative to the first decoded item.
							 * If instead you used the packet timestamps, they
							 * can be in different units depending on your
							 * IContainer, and IStream and things can get hairy
							 * quickly.
							 */
							if (firstTimestampInStream == Global.NO_PTS) {
								// This is our first time through
								firstTimestampInStream = picture.getTimeStamp();
								// get the starting clock time so we can hold up
								// frames until the right time.
								systemClockStartTime = System
										.currentTimeMillis();
							} else {
								long systemClockCurrentTime = System
										.currentTimeMillis();
								long millisecondsClockTimeSinceStartofVideo = systemClockCurrentTime
										- systemClockStartTime;

								// compute how long for this frame since the
								// first frame in the stream.
								// remember that IVideoPicture and IAudioSamples
								// timestamps are always in MICROSECONDS,
								// so we divide by 1000 to get milliseconds.
								long millisecondsStreamTimeSinceStartOfVideo = (picture
										.getTimeStamp() - firstTimestampInStream) / 1000;
								final long millisecondsTolerance = 50; // and we
																		// give
																		// ourselfs
																		// 50 ms
																		// of
																		// tolerance
								final long millisecondsToSleep = (millisecondsStreamTimeSinceStartOfVideo - (millisecondsClockTimeSinceStartofVideo + millisecondsTolerance));
								if (millisecondsToSleep > 0) {
									try {
										Thread.sleep(millisecondsToSleep);
									} catch (InterruptedException e) {
										// we might get this when the user
										// closes the dialog box, so just return
										// from the method.
										return;
									}
								}
							}

							// And finally, convert the BGR24 to an Java
							// buffered image
							BufferedImage javaImage = Utils
									.videoPictureToImage(newPic);

							// and display it on the Java Swing window
							if (listener != null)
								listener.imageUpdated(javaImage);
						}
					} // end of while
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			} else {
				/*
				 * This packet isn't part of our video stream, so we just
				 * silently drop it.
				 */
				do {
				} while (false);
			}

		}
		/*
		 * Technically since we're exiting anyway, these will be cleaned up by
		 * the garbage collector... but because we're nice people and want to be
		 * invited places for Christmas, we're going to show how to clean up.
		 */
		if (videoCoder != null) {
			videoCoder.close();
			videoCoder = null;
		}
		if (container != null) {
			container.close();
			container = null;
		}
	}
}