//Copyright © 2007-2011, PARROT SA, all rights reserved. 

//DISCLAIMER 
//The APIs is provided by PARROT and contributors "AS IS" and any express or implied warranties, including, but not limited to, the implied warranties of merchantability 
//and fitness for a particular purpose are disclaimed. In no event shall PARROT and contributors be liable for any direct, indirect, incidental, special, exemplary, or 
//consequential damages (including, but not limited to, procurement of substitute goods or services; loss of use, data, or profits; or business interruption) however 
//caused and on any theory of liability, whether in contract, strict liability, or tort (including negligence or otherwise) arising in any way out of the use of this 
//software, even if advised of the possibility of such damage. 

//Author            : Daniel Schmidt
//Publishing date   : 2010-01-06 
//based on work by  : Wilke Jansoone

//Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions
//are met:
//- Redistributions of source code must retain the above copyright notice, this list of conditions, the disclaimer and the original author of the source code.
//- Neither the name of the PixVillage Team, nor the names of its contributors may be used to endorse or promote products derived from this software without 
//  specific prior written permission.

//modified :Shigeo Yoshida, 2011-02-23
package com.shigeodayo.ardrone.video;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ReadRawFileImage {

	public ReadRawFileImage() {
	}

	public BufferedImage readUINT_RGBImage(byte[] rawData)
			throws FileNotFoundException, IOException {
		int length = 0;
		try {
			byte[] processedData = process(rawData);

			int[] pixelData = new int[processedData.length / 3];
			int raw, pixel = 0, j = 0;
			for (int i = 0; i < pixelData.length; i++) {
				pixel = 0;
				raw = processedData[j++] & 0xFF;
				pixel |= (raw << 16);
				raw = processedData[j++] & 0xFF;
				pixel |= (raw << 8);
				raw = processedData[j++] & 0xFF;
				pixel |= (raw << 0);

				pixelData[i] = pixel;
			}
			// System.out.println(pixelData.length);
			length = pixelData.length;

			if (length == 76800) {
				BufferedImage image = new BufferedImage(320, 240,
						BufferedImage.TYPE_INT_RGB);

				image.setRGB(0, 0, 320, 240, pixelData, 0, 320);
				return image;
			} else if (length == 25344) {
				BufferedImage image = new BufferedImage(176, 144,
						BufferedImage.TYPE_INT_RGB);

				image.setRGB(0, 0, 176, 144, pixelData, 0, 176);
				return image;
			}
			/*
			 * BufferedImage image = new BufferedImage(640, 480,
			 * BufferedImage.TYPE_INT_RGB);
			 * 
			 * image.setRGB(0, 0, 640, 480, pixelData, 0, 480);
			 */

		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			// System.out.println(length);
		}
		return null;
	}

	private byte[] process(final byte[] rawData) {
		final BufferedVideoImage image = new BufferedVideoImage();
		image.AddImageStream(ByteBuffer.wrap(rawData));

		final uint[] outData = image.getPixelData();

		ByteBuffer buffer = ByteBuffer.allocate(outData.length * 3);

		for (int i = 0; i < outData.length; i++) {
			int myInt = outData[i].intValue();
			buffer.put((byte) ((myInt >> 16) & 0xFF));
			buffer.put((byte) ((myInt >> 8) & 0xFF));
			buffer.put((byte) (myInt & 0xFF));

		}
		return buffer.array();
	}
}
