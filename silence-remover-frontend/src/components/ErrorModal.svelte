<script>
	export let message = '';
	export let show = true;
</script>

{#if show}
	<div class="popup">
		<p>{message}</p>
		<button on:click={() => show = false}>Close</button>
	</div>
{/if}

<style>
	.popup {
		position: fixed;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		background: red;
		padding: 1em;
		border: 1px solid black;
		z-index: 9999;
	}
</style>
