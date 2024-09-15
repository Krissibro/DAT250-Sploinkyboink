<!-- src/components/PollCard.svelte -->
<script lang="ts">
    import type { Poll } from '../types';
    export let poll: Poll;
</script>

<div class="bg-light-navy p-4 rounded shadow hover:shadow-lg transition duration-200">
    <h2 class="text-xl font-semibold mb-2 text-lightest-slate">
        <a href={`/polls/${poll.pollId}`} class="hover:underline">{poll.question}</a>
    </h2>
    <p class="text-slate mb-1">Created by: {poll.byUser.username}</p>
    <p class="text-slate mb-1">Published at: {new Date(poll.publishedAt).toLocaleString()}</p>
    <p class="text-slate">Valid until: {new Date(poll.validUntil).toLocaleString()}</p>
</div>