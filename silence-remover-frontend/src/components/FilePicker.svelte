<script lang="ts">
	import { createEventDispatcher } from 'svelte';

	let fileInput;
	let selectedFile;

	const dispatch = createEventDispatcher<{ fileSelected: [File]}>();

	const handleFileChange = (e): void => {
		const files = e.target.files;

		if (files.length > 0 && isValidFile(files[0])) {
			selectedFile = files[0];
			dispatch('fileSelected', selectedFile);
		}
	};

	const isValidFile = (file): boolean => {
		return file.type === 'video/mp4';
	};

	const selectFile = () => {
		fileInput.click();
	}
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
		margin-bottom: 1rem;
	}

	.spacer {
		flex: 1;
	}
</style>

<input type="file" accept="video/mp4" bind:this={fileInput} on:change={handleFileChange}>

<div class="container">
	{#if selectedFile}
		<h3>{selectedFile.name}</h3>
		<span class="spacer"></span>
	{/if}
	<button on:click={selectFile}>SELECT FILE</button>
</div>

