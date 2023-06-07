<script>
	export let progress;
	export let handleCancel;

	let size = 50;
	let color = "#000";
</script>

<style>
	@keyframes spin {
		0% { transform: rotate(0deg); }
		100% { transform: rotate(360deg); }
	}
	.spinner {
		animation: spin 1.5s ease-in-out infinite;
	}
	p {
		font-size: 14px;
	}
</style>

<svg class="spinner" width={size} height={size} viewBox="0 0 50 50" xmlns="http://www.w3.org/2000/svg">
	<circle class="path" cx="25" cy="25" r="20" fill="none" stroke={color} stroke-width="10" stroke-dasharray="7" />
</svg>

{#if progress}
	<p>Your video is getting stripped of its silence. Get yourself a coffee.</p>
	<p>Progress: {progress}%</p>
{:else}
	<p>Your video is getting uploaded</p>
{/if}

<button on:click={handleCancel()}>Cancel</button>
