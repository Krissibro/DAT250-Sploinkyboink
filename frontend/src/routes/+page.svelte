<!-- src/routes/+page.svelte -->

<script lang="ts">
    import type { Poll } from '$lib/types';
    import PollCard from '../components/PollCard.svelte';
    import { goto } from '$app/navigation';
    import { onMount } from 'svelte';

    export let data: { polls: Poll[]; pagination: any };

    const { polls, pagination } = data;

    // Utility function to generate page numbers
    const getPageNumbers = () => {
        const pages = [];
        for (let i = 0; i < pagination.totalPages; i++) {
            pages.push(i);
        }
        return pages;
    };

    // State to handle loading
    let isLoading = false;

    // Function to navigate to a specific page
    const navigateToPage = async (page: number) => {
        if (page === pagination.page) return; // Avoid redundant navigation
        isLoading = true;
        try {
            await goto(`/?page=${page}`, { replaceState: false });
        } catch (error) {
            console.error('Navigation error:', error);
        } finally {
            isLoading = false;
        }
    };
</script>

<div class="container mx-auto p-4">
    <h1 class="text-3xl font-bold mb-6 text-lightest-slate">Active Polls</h1>

    {#if polls.length > 0}
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {#each polls as poll}
                <PollCard {poll} />
            {/each}
        </div>

        <!-- Pagination Controls -->
        <div class="flex justify-center mt-6">
            <!-- Previous Button -->
            {#if !pagination.first}
                <button
                        on:click={() => navigateToPage(pagination.page - 1)}
                        class="px-4 py-2 mx-1 bg-light-navy text-lightest-slate rounded hover:bg-slate-700 disabled:bg-gray-600"
                        disabled={isLoading}>
                    Previous
                </button>
            {/if}

            <!-- Page Number Buttons -->
            {#each getPageNumbers() as i}
                <button
                        on:click={() => navigateToPage(i)}
                        class="px-4 py-2 mx-1 {pagination.page === i ? 'bg-slate-700' : 'bg-light-navy'} text-lightest-slate rounded hover:bg-slate-700 disabled:bg-gray-600"
                        disabled={pagination.page === i || isLoading}>
                    {i + 1}
                </button>
            {/each}

            <!-- Next Button -->
            {#if !pagination.last}
                <button
                        on:click={() => navigateToPage(pagination.page + 1)}
                        class="px-4 py-2 mx-1 bg-light-navy text-lightest-slate rounded hover:bg-slate-700 disabled:bg-gray-600"
                        disabled={isLoading}>
                    Next
                </button>
            {/if}
        </div>

        <p class="text-slate mt-4 text-center">
            Page {pagination.page + 1} of {pagination.totalPages}
        </p>
    {:else}
        <p class="text-slate text-center">No active polls available.</p>
    {/if}
</div>
