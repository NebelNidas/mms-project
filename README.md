# Silence Remover
Video editing tool that removes silent parts.

## Prerequisites
Java 17, which you can download from [here](https://adoptium.net/), and FFmpeg. Our application is theoretically cross-platform, but since we're all on Windows machines, we haven't been able to actually test it on other OSs, so you may or may not require some minute changes for it to work there. Compiled FFmpeg binaries for Windows can be downloaded [here](https://www.gyan.dev/ffmpeg/builds/#release-builds); we recommend the `ffmpeg-release-essentials` archive. The required `ffmpeg.exe` needs to be extracted from the `bin` folder.

## Usage
```shell
java -jar silence-remover.jar process \
          --input-file ./input.mp4 \
		  --output-file ./output.mp4 \
		  --ffmpeg-executable ./ffmpeg.exe
```

Additional parameters:
```
--min-segment-length: 				Minimum length in seconds a silent segment must have in order to be removed. Default: 0.4
--max-negative-volume-deviation:	Max negative deviation from the file's peak volume in decibels after which the segment is considered silent. Default: 30
--audible-segment-padding:			Padding in seconds that's added around audible segments, so the audio doesn't feel as cut off. Default: 0.25
--audio-only						Whether or not to skip video processing and only output an audio file. Speeds up processing by a lot. Default: false
--max-threads:						Rough amount of max threads Silence Remover is allowed to use. This isn't clear-cut since FFmpeg tends to use more threads than allowed. Default: Half of your system threads
--threads-per-segment				A higher value will decrease the amount of parallel FFmpeg instances, which may or may not increase processing speed depending on the input video. Default: 2
--segments-per-ffmpeg-instance		If you have lots of silent parts in a video, increasing this value will speed up processing due to less FFmpeg-instance-creation overhead. Can have the opposite effect when few silent parts are present. Default: 4
```
