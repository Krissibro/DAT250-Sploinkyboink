<script lang="ts">
    import type { Poll } from '$lib/types';
    import { currentUser } from '$stores/user';
    import { goto } from '$app/navigation';

    export let data: { poll: Poll };
    let selectedOption = '';
    let message = '';
    let user : any;
    let hasVoted = false;
    let existingVoteOption = '';

    $: user = $currentUser;

    function isLoggedIn() {
        return !!user;
    }

    $: if (isLoggedIn() && data.poll.votes) {
        const userVote = data.poll.votes.find(vote => vote.user.userID === user.userID);
        if (userVote) {
            hasVoted = true;
            existingVoteOption = userVote.voteOption;
            selectedOption = existingVoteOption;
        } else {
            hasVoted = false;
            existingVoteOption = '';
        }
    }

    async function submitVote() {
        if (!isLoggedIn()) {
            message = 'You must be logged in to vote';
            return;
        }

        const params = new URLSearchParams();
        params.append('userID', user.userID.toString());
        params.append(hasVoted ? 'newVoteOption' : 'voteOption', selectedOption);

        const method = hasVoted ? 'PUT' : 'POST';
        const res = await fetch(`/sploinkyboinkend/polls/${data.poll.pollID}/vote`, {
            method,
            body: params,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });

        if (res.ok) {
            message = hasVoted ? 'Vote updated successfully' : 'Vote registered successfully';
            // Refresh the page to reflect changes
            await goto(`/polls/${data.poll.pollID}`, { replaceState: true });
        } else {
            const errorText = await res.text();
            message = `Error: ${errorText}`;
        }
    }

    async function deleteVote() {
        if (!isLoggedIn()) {
            message = 'You must be logged in to delete your vote';
            return;
        }

        const params = new URLSearchParams();
        params.append('userID', user.userID.toString());

        const res = await fetch(`/sploinkyboinkend/polls/${data.poll.pollID}/vote`, {
            method: 'DELETE',
            body: params,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });

        if (res.ok) {
            message = 'Vote deleted successfully';
            // Refresh the page to reflect changes
            await goto(`/polls/${data.poll.pollID}`, { replaceState: true });
        } else {
            const errorText = await res.text();
            message = `Error: ${errorText}`;
        }
    }

    async function viewResults() {
        await goto(`/polls/${data.poll.pollID}/results`);
    }
</script>

<div class="container mx-auto p-4">
    <div class="bg-light-navy p-6 rounded shadow">
        <h1 class="text-3xl font-bold mb-4 text-lightest-slate">{data.poll.question}</h1>
        <p class="text-slate mb-2">Created by: {data.poll.byUser?.username || "Unknown"}</p>
        <p class="text-slate mb-2">Published at: {new Date(data.poll.publishedAt).toLocaleString()}</p>
        <p class="text-slate mb-4">Valid until: {new Date(data.poll.validUntil).toLocaleString()}</p>

        {#if isLoggedIn()}
            <form on:submit|preventDefault={submitVote}>
                <fieldset class="mb-4">
                    {#each data.poll.voteOptions as option}
                        <label class="block text-lightest-slate mb-2">
                            <input type="radio" name="voteOption" value={option} bind:group={selectedOption} class="mr-2" />
                            {option}
                        </label>
                    {/each}
                </fieldset>
                <button type="submit" class="py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded">
                    {hasVoted ? 'Update Vote' : 'Vote'}
                </button>
                {#if hasVoted}
                    <button type="button" on:click={deleteVote} class="py-2 px-4 bg-red-500 hover:bg-red-600 text-white font-semibold rounded ml-2">Delete Vote</button>
                {/if}
            </form>
            <button on:click={viewResults} class="mt-4 py-2 px-4 bg-green-600 hover:bg-green-700 text-white font-semibold rounded">View Results</button>
        {:else}
            <p class="text-slate">You must be logged in to vote. <a href="/login" class="text-blue-400 hover:underline">Login</a></p>
        {/if}

        {#if message}
            <p class="mt-4 text-slate">{message}</p>
        {/if}
    </div>
</div>
