<script lang="ts">
    import { currentUser } from "$stores/user";
    import { goto } from '$app/navigation';
    import PollForm from "$components/PollForm.svelte";

    let question = '';
    let optionsText = '';
    let validUntil = '';
    let message = '';
    let user: any;

    $: user = $currentUser;

    function isLoggedIn() {
        return !!user;
    }

    async function createPoll() {
        if (!isLoggedIn()) {
            message = 'You must be logged in to create a poll';
            return;
        }

        const voteOptions = optionsText.split('\n').map((s) => s.trim()).filter(Boolean);

        const params = new URLSearchParams();
        params.append('userID', user.userID.toString());
        params.append('question', question);
        voteOptions.forEach((option) => params.append('voteOptions', option));
        params.append('validUntil', new Date(validUntil).toISOString());

        const res = await fetch('/sploinkyboinkend/polls', {
            method: 'POST',
            body: params,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });

        if (res.ok) {
            const responseText = await res.text();
            const pollIdMatch = responseText.match(/Poll created with ID: (.+)/);
            if (pollIdMatch) {
                const pollId = pollIdMatch[1];
                await goto(`/polls/${pollId}`);
            } else {
                message = responseText;
            }
        } else {
            const errorText = await res.text();
            message = `Error: ${errorText}`;
        }
    }
</script>

<div class="container mx-auto p-4">
    <h1 class="text-3xl font-bold mb-6 text-lightest-slate">Create Poll</h1>

    {#if isLoggedIn()}
        <PollForm
                bind:question
                bind:optionsText
                bind:validUntil
                onSubmit={createPoll}
                buttonText="Create Poll"
        />
    {:else}
        <p class="text-slate mb-4">
            You must be logged in to create a poll.
            <a href="/register" class="text-blue-400 hover:underline">Register</a>
        </p>
    {/if}

    {#if message}
        <p class="mt-4 text-slate">{message}</p>
    {/if}
</div>
