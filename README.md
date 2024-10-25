# Welcome to SploinkyBoink

## Poll System Business Logic 

The business logic for this application centers around three main entities: **Users**, **Polls**, and **Votes**. Each entity has defined actions within the system, facilitated by the `PollController` and `PollService` classes. The system enforces important constraints to ensure business rules are followed.

### 1. User Management
- Users can be created, retrieved, and deleted within the system.  
- **Constraints**: A user must exist before creating a poll.

### 2. Poll Management
- Users can manage polls, including creating, retrieving, and deleting polls.  
- **Constraints**: A poll cannot be modified or deleted if it has expired or is already closed and guest users cannot create polls.  

### 3. Voting Logic
- Users can vote on a poll by choosing one of the available options.
- Users can modify their existing vote on a poll.
- Users can delete their votes.
- All votes cast in a specific poll can be retrieved. 
- **Constraints**: Users cannot vote or delete or modify their votes on polls that have expired. 

### Sequence diagram 
The sequence diagram shows the interactions involved in voting on a poll. A user either submits a vote successfully or receives an error response. 

![Sequence diagram](./images/SequenceDiagramVotingLogic.PNG)

### Voting Process Sequence:
1. User requests to vote on a poll.
2. The `PollController` handles the request and forwards it to the `PollService`.
3. The `PollService` retrieves the `Poll` and validates if the poll exists, the user exists, and the vote option is valid.
4. The vote is recorded if all conditions are met, by adding the vote to the poll and saving it to the pollRepository. 

Here is the plantUML used to create the picture:

```Java
@startuml
actor User
participant "PollController" as PC
participant "PollService" as PS
participant "VoteRepository" as VR

User -> PC: POST /polls/{pollID}/vote (userID, voteOption)
PC -> PS: voteOnPoll(pollID, userID, voteOption)

alt Poll is Active and Vote Option is Valid
    PS -> VR: save(Vote)
    VR --> PS: Vote saved
    PS --> PC: Vote registered successfully
else Poll is Expired or Vote Option is Invalid
    PS --> PC: Error (e.g., "Poll expired" or "Invalid vote option")
end

PC --> User: Response (Success or Error)
@enduml
```

## Thank you for visiting

![Cat](./images/cat.jpg)

@Krissibro, @brimlebo, @h591305 and @AudunKristian
