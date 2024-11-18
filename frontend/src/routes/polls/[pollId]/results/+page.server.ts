import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ params, fetch }) => {
    const { pollId } = params;
    const res = await fetch(`/sploinkyboinkend/polls/${pollId}/results`);
    if (res.ok) {
        const results = await res.json();
        return { results };
    } else {
        return {
            status: res.status,
            error: new Error('Results not found'),
        };
    }
};