<script>
	export let message = '';
	export let show = true;

	const hide = () => {
		show = false;
		message = null;
	};
</script>

<style>
	.backdrop {
		position: fixed;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		background-color: rgba(0, 0, 0, 0.7);
		z-index: 50;
	}

	h1 {
		margin: 0 0 20px 0;
	}

	button {
		position: absolute;
		z-index: 60;
		bottom: 10px;
		right: 20px;
		border: 0;
		background: black;
		color: white;
		padding: 5px 10px;
		font-size: 1.3rem;
	}

	.modal {
		display: block;
		width: 600px;
		max-width: 100%;
		height: 280px;
		max-height: 100%;
		position: fixed;
		z-index: 100;
		left: 50%;
		top: 50%;
		transform: translate(-50%, -50%);
		background: white;
		box-shadow: 0 0 60px 10px rgba(0, 0, 0, 0.9);
		border-radius: 8px;
	}

	.modal > div {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		overflow: auto;
		padding: 20px 50px 20px 20px;
	}

	.oops {
		color: red;
	}
</style>

{#if show}
	<div class="backdrop"></div>
	<div class="modal">
		<button on:click={hide}>Close Modal</button>
		<div class="modal-guts">
			<h1><span class="oops">Oops</span>, something went wrong!</h1>
			<p>{message}</p>
		</div>
	</div>
{/if}
