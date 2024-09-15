<script lang="ts">
    import type { Poll } from '../types';
    import PollCard from '../components/PollCard.svelte';

    export let data: { polls: Poll[] };
</script>

<div class="container mx-auto p-4">
    <h1 class="text-3xl font-bold mb-6 text-lightest-slate">All Polls</h1>

    {#if data.polls.length > 0}
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {#each data.polls as poll}
                <PollCard {poll} />
            {/each}
        </div>
    {:else}
        <p class="text-slate">No polls available.</p>
    {/if}
</div>
