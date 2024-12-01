<script lang="ts">
    import { currentUser } from '$stores/user';
    import { goto } from '$app/navigation';
    import type { Poll } from '$lib/types';
    import PollForm from "$components/PollForm.svelte";

    export let data;
    let poll: Poll = data.data;
    let user: any;
    let message = '';

    $: user = $currentUser;

    let question = poll.question;
    let optionsText = poll.voteOptions.join('\n');
    let validUntil = new Date(poll.validUntil).toISOString().slice(0, -1); // Remove 'Z' at the end

    function isLoggedIn() {
        return !!user;
    }

    async function updatePoll() {
        if (!isLoggedIn()) {
            message = 'You must be logged in to edit the poll';
            return;
        }

        const voteOptions = optionsText.split('\n').map((s) => s.trim()).filter(Boolean);

        const params = new URLSearchParams();
        params.append('question', question);
        voteOptions.forEach((option) => params.append('voteOptions', option));
        params.append('validUntil', new Date(validUntil).toISOString());

        const res = await fetch(`/sploinkyboinkend/polls/${poll.pollID}`, {
            method: 'PUT',
            body: params,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });

        if (res.ok) {
            message = 'Poll updated successfully';
            // Redirect to the poll detail page after updating
            await goto(`/polls/${poll.pollID}`);
        } else {
            const errorText = await res.text();
            message = `Error: ${errorText}`;
        }
    }
</script>

<div class="container mx-auto p-4">
    <h1 class="text-3xl font-bold mb-6 text-lightest-slate">Edit Poll</h1>

    {#if isLoggedIn() && user.username === poll.byUser?.username}
        <PollForm
                bind:question
                bind:optionsText
                bind:validUntil
                onSubmit={updatePoll}
                buttonText="Update Poll"
        />
    {:else}
        <p class="text-slate mb-4">You are not authorized to edit this poll.</p>
    {/if}

    {#if message}
        <p class="mt-4 text-slate">{message}</p>
    {/if}
</div>
