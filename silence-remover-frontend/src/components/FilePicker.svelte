<script lang="ts">
	import { createEventDispatcher } from 'svelte';

	let fileInput;
	let selectedFile;
	let duration;

	const dispatch = createEventDispatcher<{ fileSelected: [File]}>();

	const handleFileChange = (e): void => {
		const files = e.target.files;

		if (files.length > 0 && isValidFile(files[0])) {
			selectedFile = files[0];
			extractDuration(selectedFile);
			dispatch('fileSelected', selectedFile);
		}
	};

	const extractDuration = (file) => {
		let video = document.createElement('video');
		video.preload = 'metadata';
		video.onloadedmetadata = () => {
			URL.revokeObjectURL(video.src);
			duration = video.duration;
		}
		video.src = URL.createObjectURL(file);
	};

	const isValidFile = (file): boolean => {
		return file.type === 'video/mp4';
	};

	const selectFile = () => {
		fileInput.click();
	};

	const humanSize = (bytes) => {
		const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
		if (bytes == 0) return '0 B';
		const i = Math.floor(Math.log(bytes) / Math.log(1024));
		return Math.round(bytes / Math.pow(1024, i)) + ' ' + sizes[i];
	};

	const humanDuration = (seconds) => {
		if (typeof seconds !== 'number' || isNaN(seconds)) {
			return '?'
		}
		const hrs = Math.floor(seconds / 3600);
		const mins = Math.floor((seconds % 3600) / 60);
		const secs = Math.floor(seconds % 60);
		return hrs > 0 ? `${hrs}:${timeLpad(mins)}:${timeLpad(secs)}` : `${mins}:${timeLpad(secs)}`;
	};

	const timeLpad = (num) => num < 10 ? '0' + num : num;
</script>

<style>
	input[type='file'] {
		display: none;
	}

	.container {
		display: flex;
		flex-direction: row;
		justify-content: center;
		min-width: 500px;
	}

	.spacer {
		flex: 1;
	}

	h5 {
		padding: 0;
		margin: auto;
		font-weight: lighter;
	}

	.details {
		font-weight: 200;
		text-align: center;
	}

	.details p {
		margin: 0;
	}

	.file-name {
		font-size: 17px;
	}

	.detailed-details {
		color: darkgray;
	}
</style>

<input type="file" accept="video/mp4" bind:this={fileInput} on:change={handleFileChange}>

<div class="container">
	{#if selectedFile}
		<div class="details">
			<p class="file-name">{selectedFile.name}</p>
			<p class="detailed-details">{humanSize(selectedFile.size)} &nbsp; {humanDuration(duration)}</p>
		</div>
		<span class="spacer"></span>
	{/if}
	<button class="glow" on:click={selectFile}>SELECT FILE</button>
</div>
