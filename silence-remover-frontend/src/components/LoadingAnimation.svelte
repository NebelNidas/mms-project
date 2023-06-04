<script>
	export let size = 50;
	export let color = "#000";
</script>

<style>
	@keyframes spin {
		0% { transform: rotate(0deg); }
		100% { transform: rotate(360deg); }
	}
	.spinner {
		animation: spin 1s ease-in-out infinite;
	}
</style>

<svg class="spinner" width={size} height={size} viewBox="0 0 50 50" xmlns="http://www.w3.org/2000/svg">
	<circle class="path" cx="25" cy="25" r="20" fill="none" stroke={color} stroke-width="5" stroke-dasharray="10" />
</svg>
<span>Loading...</span>
