# Expass 5

Verified hash signature:
![MongoDB-verification-screenshot.png](images%2FMongoDB-verification-screenshot.png)

I decided to try using mongoDB's GUI instead of the shell as we are only just getting to know it, 
and are not really doing any difficult operations

### Experiment 1

Inserted data:
![inserted-data.png](images%2Finserted-data.png)

Queried data:
Queried for name: Alice
![queried-data.png](images%2Fqueried-data.png)

Updated data:
Changed Pauls course to Mathematics
![updated-data.png](images%2Fupdated-data.png)

Remove documents:
Removed Alice
![delete-data.png](images%2Fdelete-data.png)

Also sorry, I just now discovered dark mode in compass 😅

Bulk write: Bulk added documents
![bulkwrite-insert students.png](images%2Fbulkwrite-insert%20students.png)

![bulk inserted data.png](images%2Fbulk%20inserted%20data.png)

### Experiment 2
I recreated the map-reduce function using the Aggregation $group

![Map-reduce with compass aggregations.png](images%2FMap-reduce%20with%20compass%20aggregations.png)

### Thoughts

The map-reduce operation groups students by course and counts them, 
providing a quick overview of enrollment numbers per course. 
This is useful for analyzing student distribution and aiding in decisions like resource allocation or scheduling.

I didn't really have any issues with the assignment.