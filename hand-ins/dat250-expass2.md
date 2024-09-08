## Expass 2

I have now implemented the classes Poll, User, Vote, PollService and PollController.
I have also implemented a test for the scenarios shown in the Step 3 using MockMvc.
I tested http calls using the IntelliJ IDEA HTTP Client
I decided to handle all voting in the Poll class instead of PollService, and therefore thought it was unnecessary to have a VoteOption class.
I also added a bunch of variables that were not in the original domain model.

The development process was pretty straight forward overall as I have some experience with spring and kotlin from before, but I did have some issues.
A big issue I met was communicating with the endpoints in the PollController class, the format which I received and sent data did not work at first, as I used an object mapper to send objects as strings.
I fixed this by switching from using the object mapper to just sending the strings through RequestParams directly, and making the objects later in the Controller class instead.

I also had some issues getting spring's RestClient testing stuff to work, so I decided to use MockMvc instead by recommendation from a friend.
This worked way smoother.

I have not written api documentation or done anything with build automation.
