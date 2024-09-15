import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ params, fetch }) => {
    const { pollId } = params;
    const res = await fetch(`/api/polls/${pollId}/votes`);
    if (res.ok) {
        const votes = await res.json();
        return { votes };
    } else {
        return {
            status: res.status,
            error: new Error('Votes not found'),
        };
    }
};
