<script lang="ts">
	export let link: HTMLAnchorElement;

	const reset = () => {
		localStorage.setItem('identifier', '');
		location.reload();
	}
</script>

<h2>The Silence got removed!</h2>
<p>Click <a href={link.href} download={link.download}>here</a> if the download didn't start automatically or click <a on:click={reset}>here</a> to remove silence for another video.</p>
