<script lang="ts">
	export let link: HTMLAnchorElement;

	const reset = () => {
		localStorage.setItem('identifier', '');
		location.reload();
	}
</script>

<h2>The Silence got removed!</h2>
<p>Click <a href={link.href} download={link.download}>here</a> if the download didn't start automatically.</p>
<p>Or click <button on:click={reset}>here</button> if the download didn't start automatically.</p>
