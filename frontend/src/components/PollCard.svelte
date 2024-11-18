<!-- PollCard.svelte -->

<script lang="ts">
    import type { Poll } from '$lib/types';
    export let poll: Poll;

    // Calculate remaining time
    const timeRemaining = () => {
        const now = new Date();
        const validUntil = new Date(poll.validUntil);
        const diff = validUntil.getTime() - now.getTime();
        if (diff <= 0) return 'Poll closed';
        const days = Math.floor(diff / (1000 * 60 * 60 * 24));
        const hours = Math.floor((diff / (1000 * 60 * 60)) % 24);
        const minutes = Math.floor((diff / (1000 * 60)) % 60);
        return `${days}d ${hours}h ${minutes}m remaining`;
    };
</script>

<div class="bg-light-navy p-6 rounded-lg shadow-md hover:shadow-lg transition-all duration-300 border border-slate-700 hover:border-slate-500">
    <div class="mb-4">
        <h2 class="text-2xl font-semibold text-lightest-slate hover:text-white break-words">
            <a href={`/polls/${poll.pollID}`} class="hover:underline">{poll.question}</a>
        </h2>
    </div>

    <div class="text-slate-400 mb-3">
        <p><span class="font-semibold text-slate-300">Creator:</span> {poll.byUser?.username || "Unknown"}</p>
        <p><span class="font-semibold text-slate-300">Published on:</span> {new Date(poll.publishedAt).toLocaleString()}</p>
        <p><span class="font-semibold text-slate-300">Expires on:</span> {new Date(poll.validUntil).toLocaleString()}</p>
    </div>

    <div class="flex justify-between items-center mt-3 pt-3 border-t border-slate-600 text-slate-300">
        <p class="text-sm">{timeRemaining()}</p>
        <a href={`/polls/${poll.pollID}`} class="text-blue-500 hover:text-blue-400 font-semibold text-sm">View Poll</a>
    </div>
</div>
