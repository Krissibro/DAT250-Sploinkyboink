<!-- src/routes/polls/[pollID]/results/+page.svelte -->

<script lang="ts">
    export let data: { results: Record<string, number> };
    let totalVotes = 0;

    $: totalVotes = Object.values(data.results).reduce((sum, count) => sum + count, 0);
</script>

<div class="container mx-auto p-4">
    <h1 class="text-3xl font-bold mb-6 text-lightest-slate">Poll Results</h1>

    {#if totalVotes > 0}
        <div class="space-y-4">
            {#each Object.entries(data.results) as [option, count]}
                <div class="bg-light-navy p-4 rounded shadow">
                    <p class="text-slate mb-1"><strong>Option:</strong> {option}</p>
                    <p class="text-slate"><strong>Votes:</strong> {count}</p>
                </div>
            {/each}
        </div>
    {:else}
        <p class="text-slate">No votes have been cast yet.</p>
    {/if}
</div>