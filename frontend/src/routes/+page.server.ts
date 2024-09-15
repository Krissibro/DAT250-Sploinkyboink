import type { PageServerLoad } from './$types';
import type { Poll } from '../types';

export const load: PageServerLoad = async ({ fetch }) => {
    const res = await fetch('/api/polls');
    const polls: Poll[] = await res.json();
    return { polls };
};