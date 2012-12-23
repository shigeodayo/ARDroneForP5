package com.shigeodayo.ardrone.video;

//#region Copyright Notice

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
//specific prior written permission.

////#endregion

////#region Imports

//using System;
//using System.Collections.Generic;
//using System.Text;
////using System.Drawing;
//using System.Runtime.InteropServices;
//using System.Diagnostics;
//using System.IO;
//using System.Collections;
//using System.Threading;

//using System.Windows.Media;
//using System.Windows.Media;
//using System.Windows.Media.Imaging;

//using Wilke.Interactive.Drone.Control.Enumerations;

///#//#endregion

public class VideoImage {
	// event EventHandler<ImageCompleteEventArgs> ImageComplete;

	// [DllImport("Kernel32.dll", EntryPoint = "RtlMoveMemory")]
	// static extern void CopyMemory(IntPtr destination, IntPtr source, int
	// length);

	// /#//#region ants

	private int _BlockWidth = 8;

	private int _WidthCif = 88;
	private int _HeightCif = 72;

	private int _WidthVga = 160;
	private int _HeightVga = 120;

	private int _TableQuantization = 31;

	private int FIX_0_298631336 = 2446;
	private int FIX_0_390180644 = 3196;
	private int FIX_0_541196100 = 4433;
	private int FIX_0_765366865 = 6270;
	private int FIX_0_899976223 = 7373;
	private int FIX_1_175875602 = 9633;
	private int FIX_1_501321110 = 12299;
	private int FIX_1_847759065 = 15137;
	private int FIX_1_961570560 = 16069;
	private int FIX_2_053119869 = 16819;
	private int FIX_2_562915447 = 20995;
	private int FIX_3_072711026 = 25172;

	private int _BITS = 13;
	private int PASS1_BITS = 1;
	private int F1 = _BITS - PASS1_BITS - 1;
	private int F2 = _BITS - PASS1_BITS;
	private int F3 = _BITS + PASS1_BITS + 3;

	// /#//#endregion

	// /#//#region Private Fields

	private short[] dataBlockBuffer = new short[64];

	private short[] zigZagPositions = new short[] { 0, 1, 8, 16, 9, 2, 3, 10,
			17, 24, 32, 25, 18, 11, 4, 5, 12, 19, 26, 33, 40, 48, 41, 34, 27,
			20, 13, 6, 7, 14, 21, 28, 35, 42, 49, 56, 57, 50, 43, 36, 29, 22,
			15, 23, 30, 37, 44, 51, 58, 59, 52, 45, 38, 31, 39, 46, 53, 60, 61,
			54, 47, 55, 62, 63, };

	// Cfr. Handbook of Data Compression - Page 529
	// David Salomon
	// Giovanni Motta

	private short[] quantizerValues = new short[] { 3, 5, 7, 9, 11, 13, 15, 17,
			5, 7, 9, 11, 13, 15, 17, 19, 7, 9, 11, 13, 15, 17, 19, 21, 9, 11,
			13, 15, 17, 19, 21, 23, 11, 13, 15, 17, 19, 21, 23, 25, 13, 15, 17,
			19, 21, 23, 25, 27, 15, 17, 19, 21, 23, 25, 27, 29, 17, 19, 21, 23,
			25, 27, 29, 31 };

	static byte[] clzlut = new byte[] { 8, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4,
			4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2,
			2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
			2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	// /#//#endregion

	// /#//#region Private Properties

	private uint StreamField;
	private int StreamFieldBitIndex;
	private int StreamIndex;
	private int SliceCount;

	public int getSliceCount() {
		return SliceCount;
	}

	private boolean PictureComplete;

	private int PictureFormat;
	private int Resolution;
	private int PictureType;

	public int getPictureType() {
		return PictureType;
	}

	private int QuantizerMode;
	private int FrameIndex;

	public int getFrameIndex() {
		return FrameIndex;
	}

	private int SliceIndex;

	private int BlockCount;

	private int Width;
	private int Height;

	// / <summary>
	// / Length of one row of pixels in the destination image in bytes.
	// / </summary>
	private int PixelRowSize;

	public int getPixelRowSize() {
		return PixelRowSize;
	}

	private byte[] ImageStream;

	private ImageSlice ImageSlice;
	private uint[] PixelData;

	public uint[] getPixelData() {
		return PixelData;
	}

	// private WriteableBitmap ImageSource

	// /#//#endregion

	// /#//#region Properties

	// WriteableBitmap ImageSource
	// {
	// get { return (WriteableBitmap)ImageSource.GetAsFrozen(); }
	// }

	// /#//#endregion

	// /#//#region ruction

	public VideoImage() {
	}

	// /#//#endregion

	// /#//#region Methods

	public void AddImageStream(byte[] stream) {
		ImageStream = stream;
		ProcessStream();
	}

	// /#//#endregion

	// /#//#region Private Methods

	private void ProcessStream() {
		boolean blockY0HasAcComponents = false;
		boolean blockY1HasAcComponents = false;
		boolean blockY2HasAcComponents = false;
		boolean blockY3HasAcComponents = false;
		boolean blockCbHasAcComponents = false;
		boolean blockCrHasAcComponents = false;

		// Set StreamFieldBitIndex to 32 to make sure that the first call to
		// ReadStreamData
		// actually consumes data from the stream
		StreamFieldBitIndex = 32;
		StreamField = new uint(0);
		StreamIndex = 0;
		SliceIndex = 0;
		PictureComplete = false;

		// Stopwatch stopWatch = new Stopwatch();
		// stopWatch.Start();

		while (!PictureComplete && StreamIndex < (ImageStream.length >> 2)) {
			ReadHeader();

			if (!PictureComplete) {
				for (int count = 0; count < BlockCount; count++) {
					uint macroBlockEmpty = ReadStreamData(1);

					if (macroBlockEmpty.intValue() == (0)) {
						uint acCoefficients = ReadStreamData(8);

						blockY0HasAcComponents = acCoefficients.shiftRight(0)
								.and(1).intValue() == 1;
						blockY1HasAcComponents = acCoefficients.shiftRight(1)
								.and(1).intValue() == 1;
						blockY2HasAcComponents = acCoefficients.shiftRight(2)
								.and(1).intValue() == 1;
						blockY3HasAcComponents = acCoefficients.shiftRight(3)
								.and(1).intValue() == 1;
						blockCbHasAcComponents = acCoefficients.shiftRight(4)
								.and(1).intValue() == 1;
						blockCrHasAcComponents = acCoefficients.shiftRight(5)
								.and(1).intValue() == 1;

						if (acCoefficients.shiftRight(6).and(1).intValue() == 1) {
							uint quantizerMode = ReadStreamData(2);
							QuantizerMode = (int) ((quantizerMode.intValue() < 2) ? quantizerMode
									.flipBits() : quantizerMode.intValue());
						}

						// /#//#region Block Y0

						GetBlockBytes(blockY0HasAcComponents);
						InverseTransform(count, 0);

						// /#//#endregion

						// /#//#region Block Y1

						GetBlockBytes(blockY1HasAcComponents);
						InverseTransform(count, 1);

						// /#//#endregion

						// /#//#region Block Y2

						GetBlockBytes(blockY2HasAcComponents);
						InverseTransform(count, 2);

						// /#//#endregion

						// /#//#region Block Y3

						GetBlockBytes(blockY3HasAcComponents);
						InverseTransform(count, 3);

						// /#//#endregion

						// /#//#region Block Cb

						GetBlockBytes(blockCbHasAcComponents);
						InverseTransform(count, 4);

						// /#//#endregion

						// /#//#region Block Cr

						GetBlockBytes(blockCrHasAcComponents);
						InverseTransform(count, 5);

						// /#//#endregion
					}
				}

				ComposeImageSlice();
			}
		}

		// System.out.println("PixelData Length " + PixelData.length);
		// System.out.println("PixelRowSize " + PixelRowSize);
		// System.out.println("Width " + Width);
		// System.out.println("Height " + Height);
		// System.out.println("Length/PixelRowSize "
		// + (PixelData.length / PixelRowSize));

		/*
		 * unsafe { fixed (ushort* pixelData = PixelData) { IntPtr pixelDataPtr
		 * = (IntPtr)pixelData; ImageSource.Lock();
		 * CopyMemory(ImageSource.BackBuffer, pixelDataPtr, PixelData.Length *
		 * 2); ImageSource.AddDirtyRect(Rectangle); ImageSource.Unlock(); } }
		 * 
		 * if (ImageComplete != null) { ImageComplete(this, new
		 * ImageCompleteEventArgs(ImageSource)); }
		 */
	}

	private void ReadHeader() {
		uint code = new uint(0);
		uint startCode = new uint(0);

		AlignStreamData();

		code = ReadStreamData(22);

		startCode = new uint(code.and(~0x1F));

		if (startCode.intValue() == 32) {
			if (((code.and(0x1F).intValue()) == 0x1F)) {
				PictureComplete = true;
			} else {
				if (SliceIndex++ == 0) {
					PictureFormat = (int) ReadStreamData(2).intValue();
					Resolution = (int) ReadStreamData(3).intValue();
					PictureType = (int) ReadStreamData(3).intValue();
					QuantizerMode = (int) ReadStreamData(5).intValue();
					FrameIndex = (int) ReadStreamData(32).intValue();

					switch (PictureFormat) {
					case (int) PictureFormats.Cif:
						Width = _WidthCif << Resolution - 1;
						Height = _HeightCif << Resolution - 1;
						break;
					case (int) PictureFormats.Vga:
						Width = _WidthVga << Resolution - 1;
						Height = _HeightVga << Resolution - 1;
						break;
					}

					// We assume two bytes per pixel (RGB 565)
					PixelRowSize = Width << 1;

					SliceCount = Height >> 4;
					BlockCount = Width >> 4;

					if (ImageSlice == null) {
						ImageSlice = new ImageSlice(BlockCount);
						PixelData = new uint[Width * Height];
						// ImageSource = new WriteableBitmap(Width, Height, 96,
						// 96, PixelFormats.Bgr565, null);
						// Rectangle = new Int32Rect(0, 0, Width, Height);
					} else {
						if (ImageSlice.MacroBlocks.length != BlockCount) {
							ImageSlice = new ImageSlice(BlockCount);
							PixelData = new uint[Width * Height];
							// ImageSource = new WriteableBitmap(Width, Height,
							// 96, 96, PixelFormats.Bgr565, null);
							// Rectangle = new Int32Rect(0, 0, Width, Height);
						}
					}
				} else {
					QuantizerMode = (int) ReadStreamData(5).intValue();
				}
			}
		}
	}

	private void GetBlockBytes(boolean acCoefficientsAvailable) {
		int[] run = new int[] { 0 };
		int[] level = new int[] { 0 };
		int zigZagPosition = 0;
		int matrixPosition = 0;
		boolean[] last = new boolean[] { false };

		for (int i = 0; i < dataBlockBuffer.length; i++) {
			dataBlockBuffer[i] = 0;
		}
		// Array.Clear(dataBlockBuffer, 0, dataBlockBuffer.length);

		uint dcCoefficient = ReadStreamData(10);

		if (QuantizerMode == _TableQuantization) {
			dataBlockBuffer[0] = (short) (dcCoefficient
					.times(quantizerValues[0]));

			if (acCoefficientsAvailable) {
				DecodeFieldBytes(run, level, last);

				while (!last[0]) {
					zigZagPosition += run[0] + 1;
					matrixPosition = zigZagPositions[zigZagPosition];
					level[0] *= quantizerValues[matrixPosition];
					dataBlockBuffer[matrixPosition] = (short) level[0];
					DecodeFieldBytes(run, level, last);
				}
			}
		} else {
			// Currently not implemented.
			throw new RuntimeException(
					"ant quantizer mode is not yet implemented.");
		}
	}

	private void DecodeFieldBytes(int[] run, int[] level, boolean[] last) {
		uint streamCode = new uint(0);

		int streamLength = 0;
		;
		int zeroCount = 0;
		int temp = 0;
		int sign = 0;

		// Use the RLE and Huffman dictionaries to understand this code
		// fragment. You can find
		// them in the developers guide on page 34.
		// The bits in the data are actually composed of two kinds of fields:
		// - run fields - this field contains information on the number of
		// consecutive zeros.
		// - level fields - this field contains the actual non zero value which
		// can be negative or positive.
		// First we extract the run field info and then the level field info.

		streamCode = PeekStreamData(ImageStream, 32);

		// /#//#region Determine number of consecutive zeros in zig zag. (a.k.a
		// 'run' field info)

		// Suppose we have following bit sequence:
		// 00001111.....
		// 1 - Count the number of leading zeros -> 4
		// Coarse value lookup is thus 00001
		// 2 - Lookup the additional value, for coarse value 00001 this is 3
		// addtional bits
		// 3 - Calculate value of run, for coarse value 00001 this is (111) + 8

		zeroCount = CountLeadingZeros(streamCode); // - (1)
		streamCode.shiftLeftEquals(zeroCount + 1); // - (2) -> shift left to get
		// rid of the coarse value
		streamLength += zeroCount + 1; // - position bit pointer to keep track
		// off how many bits to consume later on
		// the stream.

		if (zeroCount > 1) {
			temp = (streamCode.shiftRight(32 - (zeroCount - 1))).intValue(); // -
			// (2)
			// ->
			// shift
			// right
			// to
			// determine
			// the
			// addtional
			// bits
			// (number
			// of
			// additional
			// bits
			// is
			// zerocount
			// - 1)
			streamCode.shiftLeftEquals(zeroCount - 1); // - shift all of the run
			// bits out of the way
			// so the first bit is
			// points to the first
			// bit of the level
			// field.
			streamLength += zeroCount - 1;// - position bit pointer to keep
			// track off how many bits to
			// consume later on the stream.
			run[0] = temp + (1 << (zeroCount - 1)); // - (3) -> calculate run
			// value
		} else {
			run[0] = zeroCount;
		}

		// /#//#endregion

		// /#//#region Determine non zero value. (a.k.a 'level' field info)

		// Suppose we have following bit sequence:
		// 000011111.....
		// 1 - Count the number of leading zeros -> 4
		// Coarse value lookup is thus 00001
		// 2 - Lookup the additional value, for coarse value 00001 this is 4
		// addtional bits (last bit is sign bit)
		// 3 - Calculate value of run, for coarse value 00001 this is (xxx) + 8,
		// multiply by sign

		zeroCount = CountLeadingZeros(streamCode);
		streamCode.shiftLeftEquals(zeroCount + 1); // - (1)
		streamLength += zeroCount + 1; // - position bit pointer to keep track
		// off how many bits to consume later on
		// the stream.

		if (zeroCount == 1) {
			// If coarse value is 01 according to the Huffman dictionary this
			// means EOB, so there is
			// no run and level and we indicate this by setting last to true;
			run[0] = 0;
			last[0] = true;
		} else {
			if (zeroCount == 0) {
				zeroCount = 1;
				temp = 1;
			}

			streamLength += zeroCount;// - position bit pointer to keep track
			// off how many bits to consume later on
			// the stream.
			streamCode.shiftRightEquals(32 - zeroCount);// - (2) -> shift right
			// to determine the
			// addtional bits
			// (number of additional
			// bits is zerocount)
			// sign = (sbyte)(streamCode & 1); // determine sign, last bit is
			// sign
			sign = (int) (streamCode.and(1).intValue()); // determine sign, last
			// bit is sign

			if (zeroCount != 0) {
				// temp = (sbyte)(streamCode >> 1); // take into account that
				// last bit is sign, so shift it out of the way
				// temp += (sbyte)(1 << (zeroCount - 1)); // - (3) -> calculate
				// run value without sign
				temp = (streamCode.shiftRight(1)).intValue(); // take into
				// account
				// that last bit is
				// sign, so shift it
				// out of the way
				temp += (int) (1 << (zeroCount - 1)); // - (3) -> calculate run
				// value without sign
			}

			level[0] = (sign == 1) ? -temp : temp; // - (3) -> calculate run
			// value with sign
			last[0] = false;
		}

		// /#//#endregion

		ReadStreamData(streamLength);
	}

	int numCalls = 0;

	private uint ReadStreamData(int count) {
		uint data = new uint(0);

		while (count > (32 - StreamFieldBitIndex)) {
			data = (data.shiftLeft((int) (32 - StreamFieldBitIndex))
					.or(StreamField.shiftRight(StreamFieldBitIndex)));

			count -= 32 - StreamFieldBitIndex;

			StreamField = new uint(ImageStream, StreamIndex * 4);

			StreamFieldBitIndex = 0;
			StreamIndex++;
		}

		if (count > 0) {
			data = data.shiftLeft(count).or(StreamField.shiftRight(32 - count));

			StreamField.shiftLeftEquals(count);
			StreamFieldBitIndex += count;
		}

		numCalls++;
		// System.out.println("ReadStreamData " + data + " " + numCalls + " " +
		// count);

		return data;
	}

	private uint PeekStreamData(byte[] stream, int count) {
		uint data = new uint(0);
		uint streamField = StreamField;
		int streamFieldBitIndex = StreamFieldBitIndex;

		while (count > (32 - streamFieldBitIndex)
				&& StreamIndex < (ImageStream.length >> 2)) {
			data = (data.shiftLeft(32 - streamFieldBitIndex)).or(streamField
					.shiftRight(streamFieldBitIndex));

			count -= 32 - streamFieldBitIndex;

			streamField = new uint(stream, StreamIndex * 4);
			streamFieldBitIndex = 0;
		}

		if (count > 0) {
			data = data.shiftLeft(count).or(
					streamField.shiftRight((32 - count)));
		}

		return data;
	}

	private void AlignStreamData() {
		int alignedLength;
		int actualLength;

		actualLength = StreamFieldBitIndex;

		if (actualLength > 0) {
			alignedLength = (actualLength & ~7);
			if (alignedLength != actualLength) {
				alignedLength += 0x08;
				StreamField.shiftLeftEquals(alignedLength - actualLength);
				StreamFieldBitIndex = alignedLength;
			}
		}
	}

	// Blockline:
	// _______
	// | 1 | 2 |
	// |___|___| Y
	// | 3 | 4 |
	// |___|___|
	// ___
	// | 5 |
	// |___| Cb
	// ___
	// | 6 |
	// |___| Cr
	//
	// Layout in memory
	// _______________________
	// | 1 | 2 | 3 | 4 | 5 | 6 | ...
	// |___|___|___|___|___|___|
	//

	// Example, suppose the six data sub blocks are as follows:

	// ==============Y0============== ==============Y1==============
	// ==============Y2============== ==============Y3==============

	// 0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
	// 0, 1, 2, 3, 4, 5, 6, 7,
	// 8, 9, 10, 11, 12, 13, 14, 15, 8, 9, 10, 11, 12, 13, 14, 15, 8, 9, 10, 11,
	// 12, 13, 14, 15, 8, 9, 10, 11, 12, 13, 14, 15,
	// 16, 17, 18, 19, 20, 21, 22, 23, 16, 17, 18, 19, 20, 21, 22, 23, 16, 17,
	// 18, 19, 20, 21, 22, 23, 16, 17, 18, 19, 20, 21, 22, 23,
	// 24, 25, 26, 27, 28, 29, 30, 31, 24, 25, 26, 27, 28, 29, 30, 31, 24, 25,
	// 26, 27, 28, 29, 30, 31, 24, 25, 26, 27, 28, 29, 30, 31,
	// 32, 33, 34, 35, 36, 37, 38, 39, 32, 33, 34, 35, 36, 37, 38, 39, 32, 33,
	// 34, 35, 36, 37, 38, 39, 32, 33, 34, 35, 36, 37, 38, 39,
	// 40, 41, 42, 43, 44, 45, 46, 47, 40, 41, 42, 43, 44, 45, 46, 47, 40, 41,
	// 42, 43, 44, 45, 46, 47, 40, 41, 42, 43, 44, 45, 46, 47,
	// 48, 49, 50, 51, 52, 53, 54, 55, 48, 49, 50, 51, 52, 53, 54, 55, 48, 49,
	// 50, 51, 52, 53, 54, 55, 48, 49, 50, 51, 52, 53, 54, 55,
	// 56, 57, 58, 59, 60, 61, 62, 63, 56, 57, 58, 59, 60, 61, 62, 63, 56, 57,
	// 58, 59, 60, 61, 62, 63, 56, 57, 58, 59, 60, 61, 62, 63

	// ==============Cb============== ==============Cr==============

	// 0, 1, 2, 3, | 4, 5, 6, 7, 0, 1, 2, 3, | 4, 5, 6, 7,
	// 8, 9, 10, 11, | 12, 13, 14, 15, 8, 9, 10, 11, | 12, 13, 14, 15,
	// 16, 17, 18, 19, | 20, 21, 22, 23, 16, 17, 18, 19, | 20, 21, 22, 23,
	// 24, 25, 26, 27, | 28, 29, 30, 31, 24, 25, 26, 27, | 28, 29, 30, 31,
	// ----------------| --------------- --------------- | ---------------
	// 32, 33, 34, 35, | 36, 37, 38, 39, 32, 33, 34, 35, | 36, 37, 38, 39,
	// 40, 41, 42, 43, | 44, 45, 46, 47, 40, 41, 42, 43, | 44, 45, 46, 47,
	// 48, 49, 50, 51, | 52, 53, 54, 55, 48, 49, 50, 51, | 52, 53, 54, 55,
	// 56, 57, 58, 59, | 60, 61, 62, 63, 56, 57, 58, 59, | 60, 61, 62, 63,

	// Pixel Matrix

	// 0, 1, 2, 3, 4, 5, 6, 7, | 8, 9, 10, 11, 12, 13, 14, 15,
	// 16, 17, 18, 19, 20, 21, 22, 23, | 24, 25, 26, 27, 28, 29, 30, 31,
	// 32, 33, 34, 35, 36, 37, 38, 39, | 40, 41, 42, 43, 44, 45, 46, 47,
	// 48, 49, 50, 51, 52, 53, 54, 55, | 56, 57, 58, 59, 60, 61, 62, 63,
	// 64, 65, 66, 67, 68, 69, 70, 71, | 72, 73, 74, 75, 76, 77, 78, 79,
	// 80, 81, 82, 83, 84, 85, 86, 87, | 88, 89, 90, 91, 92, 93, 94, 95,
	// 96, 97, 98, 99, 100, 101, 102, 103, | 104, 105, 106, 107, 108, 109, 110,
	// 111,
	// 112, 113, 114, 115, 116, 117, 118, 119, | 120, 121, 122, 123, 124, 125,
	// 126, 127,
	// ----------------------------------------|---------------------------------------
	// 128, 129, 130, 131, 132, 133, 134, 135, | 136, 137, 138, 139, 140, 141,
	// 142, 143,
	// 144, 145, 146, 147, 148, 149, 150, 151, | 152, 153, 154, 155, 156, 157,
	// 158, 159,
	// 160, 161, 162, 163, 164, 165, 166, 167, | 168, 169, 170, 171, 172, 173,
	// 174, 175,
	// 176, 177, 178, 179, 180, 181, 182, 183, | 184, 185, 186, 187, 188, 189,
	// 190, 191,
	// 192, 193, 194, 195, 196, 197, 198, 199, | 200, 201, 202, 203, 204, 205,
	// 206, 207,
	// 208, 209, 210, 211, 212, 213, 214, 215, | 216, 217, 218, 219, 220, 221,
	// 222, 223,
	// 224, 225, 226, 227, 228, 229, 230, 231, | 232, 233, 234, 235, 236, 237,
	// 238, 239,
	// 240, 241, 242, 243, 244, 245, 246, 247, | 248, 249, 250, 251, 252, 253,
	// 254, 255,

	// The four Luma 8x8 matrices (quadrants Y0, Y1, Y2, Y3) form the basis of
	// the final 16x16 pixel matrix.
	// The two Croma 8x8 matrices are used to calculate the actual RGB value of
	// the pixel (RGB565, each pixel is represented by two bytes)

	// Each processing loop processes from each Luma matrix two rows. In each
	// 'two row' loop the rows are processed
	// by two columns.

	// First Loop will take (assume there is only one pixel matrix to fill):

	// Quadrant 1
	// From Cb -> 0
	// From Cr -> 0
	// From Y0 -> 0, 8 and 1, 9 - use Cb and Cr to calculate RGB and place in
	// pixel matrix in 0, 16 and 1 and 17

	// Quadrant 2
	// From Cb -> 4
	// From Cr -> 4
	// From Y1 -> 0, 8 and 1, 9 - use Cb and Cr to calculate RGB and place in
	// pixel matrix in 8, 24 and 9 and 25

	// Quadrant 3
	// From Cb -> 32
	// From Cr -> 32
	// From Y2 -> 0, 8 and 1, 9 - use Cb and Cr to calculate RGB and place in
	// pixel matrix in 128, 144 and 129 and 145

	// Quadrant 4
	// From Cb -> 36
	// From Cr -> 36
	// From Y3 -> 0, 8 and 1, 9 - use Cb and Cr to calculate RGB and place in
	// pixel matrix in 136, 152 and 137 and 153

	// Second Loop will take (assume there is only one pixel matrix to fill):

	// Quadrant 1
	// From Cb -> 1
	// From Cr -> 1
	// From Y0 -> 2, 10 and 3, 11 - use Cb and Cr to calculate RGB and place in
	// pixel matrix in 2, 18 and 3 and 19

	// Quadrant 2
	// From Cb -> 5
	// From Cr -> 5
	// From Y1 -> 2, 10 and 3, 11 - use Cb and Cr to calculate RGB and place in
	// pixel matrix in 10, 26 and 11 and 27

	// Quadrant 3
	// From Cb -> 33
	// From Cr -> 33
	// From Y2 -> 2, 10 and 3, 11 - use Cb and Cr to calculate RGB and place in
	// pixel matrix in 130, 146 and 131 and 147
	// Quadrant 4
	// From Cb -> 37
	// From Cr -> 37
	// From Y3 -> 2, 10 and 3, 11 - use Cb and Cr to calculate RGB and place in
	// pixel matrix in 138, 154 and 139 and 155

	// We need third and fourth loop to complete first two lines of the luma
	// blocks. At this time we
	// have written 64 pixels to the pixel matrix.

	// These four loops have to be repeated 4 more times (4 * 64 = 256) to fill
	// complete pixel matrix.

	// Remark the offsets to use in the pixel matrix have to take into account
	// that an GroupOfBlocks contains multiple pixel matrices.
	// So to calculate the real index we have to take that also into account
	// (BlockCount)

	private void ComposeImageSlice() {
		int u, ug, ub;
		int v, vg, vr;
		int r, g, b;

		int lumaElementIndex1 = 0;
		int lumaElementIndex2 = 0;
		int chromaOffset = 0;

		int dataIndex1 = 0;
		int dataIndex2 = 0;

		int lumaElementValue1 = 0;
		int lumaElementValue2 = 0;
		int chromaBlueValue = 0;
		int chromaRedValue = 0;

		int[] cromaQuadrantOffsets = new int[] { 0, 4, 32, 36 };
		int[] pixelDataQuadrantOffsets = new int[] { 0, _BlockWidth,
				Width * _BlockWidth, (Width * _BlockWidth) + _BlockWidth };

		int imageDataOffset = (SliceIndex - 1) * Width * 16;

		for (MacroBlock macroBlock : ImageSlice.MacroBlocks) {
			for (int verticalStep = 0; verticalStep < _BlockWidth / 2; verticalStep++) {
				chromaOffset = verticalStep * _BlockWidth;
				lumaElementIndex1 = verticalStep * _BlockWidth * 2;
				lumaElementIndex2 = lumaElementIndex1 + _BlockWidth;

				dataIndex1 = imageDataOffset + (2 * verticalStep * Width);
				dataIndex2 = dataIndex1 + Width;

				for (int horizontalStep = 0; horizontalStep < _BlockWidth / 2; horizontalStep++) {
					for (int quadrant = 0; quadrant < 4; quadrant++) {
						int chromaIndex = chromaOffset
								+ cromaQuadrantOffsets[quadrant]
								+ horizontalStep;
						chromaBlueValue = macroBlock.DataBlocks[4][chromaIndex];
						chromaRedValue = macroBlock.DataBlocks[5][chromaIndex];

						u = chromaBlueValue - 128;
						ug = 88 * u;
						ub = 454 * u;

						v = chromaRedValue - 128;
						vg = 183 * v;
						vr = 359 * v;

						for (int pixel = 0; pixel < 2; pixel++) {
							int deltaIndex = 2 * horizontalStep + pixel;
							lumaElementValue1 = macroBlock.DataBlocks[quadrant][lumaElementIndex1
									+ deltaIndex] << 8;
							lumaElementValue2 = macroBlock.DataBlocks[quadrant][lumaElementIndex2
									+ deltaIndex] << 8;

							r = Saturate5(lumaElementValue1 + vr);
							g = Saturate6(lumaElementValue1 - ug - vg);
							b = Saturate5(lumaElementValue1 + ub);

							PixelData[dataIndex1
									+ pixelDataQuadrantOffsets[quadrant]
									+ deltaIndex] = MakeRgb(r, g, b);

							r = Saturate5(lumaElementValue2 + vr);
							g = Saturate6(lumaElementValue2 - ug - vg);
							b = Saturate5(lumaElementValue2 + ub);

							PixelData[dataIndex2
									+ pixelDataQuadrantOffsets[quadrant]
									+ deltaIndex] = MakeRgb(r, g, b);
						}
					}
				}
			}

			imageDataOffset += 16;
		}
	}

	private int Saturate5(int x) {
		if (x < 0) {
			x = 0;
		}

		x >>= 11;

		return (x > 0x1F) ? 0x1F : x;
	}

	private int Saturate6(int x) {
		if (x < 0) {
			x = 0;
		}

		x >>= 10;

		return x > 0x3F ? 0x3F : x;
	}

	private uint MakeRgb(int r, int g, int b) {
		r <<= 2;
		g <<= 1;
		b <<= 2;

		uint ru = new uint(r);
		uint gu = new uint(g);
		uint bu = new uint(b);

		uint retval = ru.shiftLeft(16);
		retval = retval.or(gu.shiftLeft(8));
		retval = retval.or(bu);

		return retval;
		// return new newUint((r << 16) | (g << 8) | b);
	}

	private int CountLeadingZeros(uint value) {
		int accum = 0;

		accum += clzlut[value.shiftRight(24).intValue()];
		if (accum == 8) {
			accum += clzlut[(value.shiftRight(16).intValue()) & 0xFF];
		}
		if (accum == 16) {
			accum += clzlut[(value.shiftRight(8).intValue()) & 0xFF];
		}
		if (accum == 24) {
			accum += clzlut[value.intValue() & 0xFF];
		}

		return accum;
	}

	// /#//#region Dct Methods

	void InverseTransform(int macroBlockIndex, int dataBlockIndex) {
		int[] workSpace = new int[64];
		short[] data = new short[64];

		int z1, z2, z3, z4, z5;
		int tmp0, tmp1, tmp2, tmp3;
		int tmp10, tmp11, tmp12, tmp13;

		int pointer = 0;

		for (int index = 8; index > 0; index--) {
			if (dataBlockBuffer[pointer + 8] == 0
					&& dataBlockBuffer[pointer + 16] == 0
					&& dataBlockBuffer[pointer + 24] == 0
					&& dataBlockBuffer[pointer + 32] == 0
					&& dataBlockBuffer[pointer + 40] == 0
					&& dataBlockBuffer[pointer + 48] == 0
					&& dataBlockBuffer[pointer + 56] == 0) {
				int dcValue = dataBlockBuffer[pointer] << PASS1_BITS;

				workSpace[pointer + 0] = dcValue;
				workSpace[pointer + 8] = dcValue;
				workSpace[pointer + 16] = dcValue;
				workSpace[pointer + 24] = dcValue;
				workSpace[pointer + 32] = dcValue;
				workSpace[pointer + 40] = dcValue;
				workSpace[pointer + 48] = dcValue;
				workSpace[pointer + 56] = dcValue;

				pointer++;
				continue;
			}

			z2 = dataBlockBuffer[pointer + 16];
			z3 = dataBlockBuffer[pointer + 48];

			z1 = (z2 + z3) * FIX_0_541196100;
			tmp2 = z1 + z3 * -FIX_1_847759065;
			tmp3 = z1 + z2 * FIX_0_765366865;

			z2 = dataBlockBuffer[pointer];
			z3 = dataBlockBuffer[pointer + 32];

			tmp0 = (z2 + z3) << _BITS;
			tmp1 = (z2 - z3) << _BITS;

			tmp10 = tmp0 + tmp3;
			tmp13 = tmp0 - tmp3;
			tmp11 = tmp1 + tmp2;
			tmp12 = tmp1 - tmp2;

			tmp0 = dataBlockBuffer[pointer + 56];
			tmp1 = dataBlockBuffer[pointer + 40];
			tmp2 = dataBlockBuffer[pointer + 24];
			tmp3 = dataBlockBuffer[pointer + 8];

			z1 = tmp0 + tmp3;
			z2 = tmp1 + tmp2;
			z3 = tmp0 + tmp2;
			z4 = tmp1 + tmp3;
			z5 = (z3 + z4) * FIX_1_175875602;

			tmp0 = tmp0 * FIX_0_298631336;
			tmp1 = tmp1 * FIX_2_053119869;
			tmp2 = tmp2 * FIX_3_072711026;
			tmp3 = tmp3 * FIX_1_501321110;
			z1 = z1 * -FIX_0_899976223;
			z2 = z2 * -FIX_2_562915447;
			z3 = z3 * -FIX_1_961570560;
			z4 = z4 * -FIX_0_390180644;

			z3 += z5;
			z4 += z5;

			tmp0 += z1 + z3;
			tmp1 += z2 + z4;
			tmp2 += z2 + z3;
			tmp3 += z1 + z4;

			workSpace[pointer + 0] = ((tmp10 + tmp3 + (1 << F1)) >> F2);
			workSpace[pointer + 56] = ((tmp10 - tmp3 + (1 << F1)) >> F2);
			workSpace[pointer + 8] = ((tmp11 + tmp2 + (1 << F1)) >> F2);
			workSpace[pointer + 48] = ((tmp11 - tmp2 + (1 << F1)) >> F2);
			workSpace[pointer + 16] = ((tmp12 + tmp1 + (1 << F1)) >> F2);
			workSpace[pointer + 40] = ((tmp12 - tmp1 + (1 << F1)) >> F2);
			workSpace[pointer + 24] = ((tmp13 + tmp0 + (1 << F1)) >> F2);
			workSpace[pointer + 32] = ((tmp13 - tmp0 + (1 << F1)) >> F2);

			pointer++;
		}

		pointer = 0;

		for (int index = 0; index < 8; index++) {
			z2 = workSpace[pointer + 2];
			z3 = workSpace[pointer + 6];

			z1 = (z2 + z3) * FIX_0_541196100;
			tmp2 = z1 + z3 * -FIX_1_847759065;
			tmp3 = z1 + z2 * FIX_0_765366865;

			tmp0 = (workSpace[pointer + 0] + workSpace[pointer + 4]) << _BITS;
			tmp1 = (workSpace[pointer + 0] - workSpace[pointer + 4]) << _BITS;

			tmp10 = tmp0 + tmp3;
			tmp13 = tmp0 - tmp3;
			tmp11 = tmp1 + tmp2;
			tmp12 = tmp1 - tmp2;

			tmp0 = workSpace[pointer + 7];
			tmp1 = workSpace[pointer + 5];
			tmp2 = workSpace[pointer + 3];
			tmp3 = workSpace[pointer + 1];

			z1 = tmp0 + tmp3;
			z2 = tmp1 + tmp2;
			z3 = tmp0 + tmp2;
			z4 = tmp1 + tmp3;

			z5 = (z3 + z4) * FIX_1_175875602;

			tmp0 = tmp0 * FIX_0_298631336;
			tmp1 = tmp1 * FIX_2_053119869;
			tmp2 = tmp2 * FIX_3_072711026;
			tmp3 = tmp3 * FIX_1_501321110;
			z1 = z1 * -FIX_0_899976223;
			z2 = z2 * -FIX_2_562915447;
			z3 = z3 * -FIX_1_961570560;
			z4 = z4 * -FIX_0_390180644;

			z3 += z5;
			z4 += z5;

			tmp0 += z1 + z3;
			tmp1 += z2 + z4;
			tmp2 += z2 + z3;
			tmp3 += z1 + z4;

			data[pointer + 0] = (short) ((tmp10 + tmp3) >> F3);
			data[pointer + 7] = (short) ((tmp10 - tmp3) >> F3);
			data[pointer + 1] = (short) ((tmp11 + tmp2) >> F3);
			data[pointer + 6] = (short) ((tmp11 - tmp2) >> F3);
			data[pointer + 2] = (short) ((tmp12 + tmp1) >> F3);
			data[pointer + 5] = (short) ((tmp12 - tmp1) >> F3);
			data[pointer + 3] = (short) ((tmp13 + tmp0) >> F3);
			data[pointer + 4] = (short) ((tmp13 - tmp0) >> F3);

			pointer += 8;
		}

		for (int i = 0; i < data.length; i++) {
			ImageSlice.MacroBlocks[macroBlockIndex].DataBlocks[dataBlockIndex][i] = data[i];
		}
		/*
		 * unsafe { fixed (short* source = data) fixed (short* destination =
		 * ImageSlice.MacroBlocks[macroBlockIndex].DataBlocks[dataBlockIndex]) {
		 * IntPtr sourcePtr = (IntPtr)source; IntPtr destinationPtr =
		 * (IntPtr)destination; CopyMemory(destinationPtr, sourcePtr,
		 * data.Length * 2); } }
		 */
	}
}

// /#//#endregion

// /#//#endregion
