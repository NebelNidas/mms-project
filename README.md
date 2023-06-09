# Silence Remover
Video editing tool that removes silent parts.

## Prerequisites
Java 17 and FFmpeg.

## Usage
`java -jar process --input-file input.mp4 --output-file output.mp4 --ffmpeg-executable ffmpeg.exe`

Additional parameters:
```
--min-segment-length: 				Minimum length in seconds a silent segment is allowed to have in order to be removed.
--max-negative-volume-deviation:	All segments below this deviation are considered silent.
--audible-segment-padding:			Padding in seconds that's added around audible segments, so the audio doesn't feel as cut off.
--audio-only						Whether or not to skip video processing and only output an audio file.
--max-threads:						Max threads Silence Remover is allowed to use (currently borked).
--threads-per-ffmpeg-instance
--segments-per-ffmpeg-instance
```
