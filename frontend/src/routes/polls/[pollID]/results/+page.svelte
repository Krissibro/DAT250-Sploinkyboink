<!-- src/routes/polls/[pollID]/results/+page.svelte -->

<script lang="ts">
    export let data: { results: Record<string, number>, question: string };
    let totalVotes = 0;
    let sortedResults = [];

    $: totalVotes = Object.values(data.results).reduce((sum, count) => sum + count, 0);

    $: sortedResults = Object.entries(data.results).sort((a, b) => b[1] - a[1]);
</script>

<div class="container mx-auto p-4">
    <h1 class="text-3xl font-bold mb-6 text-lightest-slate">Poll Results</h1>

    {#if totalVotes > 0}
        <p class="text-slate mb-4">Total Votes: {totalVotes}</p>
        <div class="space-y-4">
            {#each sortedResults as [option, count]}
                <div>
                    <div class="flex justify-between mb-1">
                        <span class="text-lightest-slate">{option}</span>
                        <span class="text-slate">{((count / totalVotes) * 100).toFixed(1)}% ({count} votes)</span>
                    </div>
                    {#if count !== 0}
                        <div class="w-full bg-slate-700 rounded-full h-4">
                            <div class="bg-blue-600 h-4 rounded-full" style="width: {(count / totalVotes) * 100}%"></div>
                        </div>
                    {:else}
                        <div class="w-full bg-slate-700 rounded-full h-4">
                            <div class="bg-transparent border border-dashed border-blue-500 h-4 rounded-full opacity-40"></div>
                        </div>
                    {/if}
                </div>
            {/each}
        </div>
    {:else}
        <p class="text-slate">No votes have been cast yet.</p>
    {/if}
</div>

