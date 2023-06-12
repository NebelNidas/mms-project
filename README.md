# Silence Remover
Video editing tool that removes silent segments.

## Prerequisites
Java 17, which you can download from [here](https://adoptium.net/), and FFmpeg. Our application is theoretically cross-platform, but since we're all on Windows machines, we haven't been able to actually test it on other OSs, so you may or may not require some minute changes for it to work there. Compiled FFmpeg binaries for Windows can be downloaded [here](https://www.gyan.dev/ffmpeg/builds/#release-builds); we recommend the `ffmpeg-release-essentials` archive. The required `ffmpeg.exe` needs to be extracted from the `bin` folder.

## Usage
```shell
java -jar silence-remover-cli.jar process \
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

## Known issues
- Multithreading performs rather poorly at the moment. Using input videos with a length over 5 minutes isn't recommended for that reason yet. We're continuing to look into potential solutions though (filters seem like a good candidate).
- Some output videos appear to have the same or an even greater length as their input counterparts after processing. This seems to be an issue with FFmpeg's concatenate command though, as the actual video length is shorter, it's just the metadata that's wrong sometimes.

## Building
The project can be built via `./gradlew :<subproject>:build`, for example `./gradlew :silence-remover-cli:build`. The built JAR files are in the respective module's `build/libs` folder. The `*-all` JARs have all dependencies bundled in.

The server can be built this way too, but currently there are now ways to customize its default settings via the CLI yet, so you'd have to edit them directly in the source files.

The frontend can't be built built automatically yet either, you have to have NodeJS 18 installed and then run `npm install`, `npm run build` and `npm run start` (in this order). It will be available at port 8080.
