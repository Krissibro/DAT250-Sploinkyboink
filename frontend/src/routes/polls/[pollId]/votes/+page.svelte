<script lang="ts">
    import type { Vote } from '../../../../types';

    export let data: { votes: Record<string, Vote> };

    const votesArray = Object.entries(data.votes);
</script>

<div class="container mx-auto p-4">
    <h1 class="text-3xl font-bold mb-6 text-lightest-slate">Votes</h1>

    {#if votesArray.length > 0}
        <div class="space-y-4">
            {#each votesArray as [username, vote]}
                <div class="bg-light-navy p-4 rounded shadow">
                    <p class="text-slate mb-1"><strong>User:</strong> {username}</p>
                    <p class="text-slate mb-1"><strong>Option:</strong> {vote.voteOption}</p>
                    <p class="text-slate"><strong>Time:</strong> {new Date(vote.publishedAt).toLocaleString()}</p>
                </div>
            {/each}
        </div>
    {:else}
        <p class="text-slate">No votes yet.</p>
    {/if}
</div>
