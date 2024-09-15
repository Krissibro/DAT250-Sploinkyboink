export interface User {
    username: string;
    email: string;
}

export interface Vote {
    voteOption: string;
    publishedAt: string; // ISO date string
}

export interface Poll {
    pollId: string;
    byUser: User;
    question: string;
    publishedAt: string; // ISO date string
    validUntil: string; // ISO date string
    voteOptions: string[];
    votes?: Record<string, Vote>;
}
