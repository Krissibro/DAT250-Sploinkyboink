import type { PageServerLoad } from './$types';
import type { Poll } from '../../../types';

export const load: PageServerLoad = async ({ params, fetch }) => {
    const { pollId } = params;
    const res = await fetch(`/api/polls`);
    const polls: Poll[] = await res.json();
    const poll = polls.find((p) => p.pollId === pollId);

    if (poll) {
        return { poll };
    } else {
        return {
            status: 404,
            error: new Error('Poll not found'),
        };
    }
};
