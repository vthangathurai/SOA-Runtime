ItemSearch is a demo web server application.  DemoApp is the Web app project.  It hosts a service call ItemSerachService.  The ItemSearchService has one operation called findByKeywords.  This operation takes keywords from request, for example, 'iPod'. It calls eBay public service and Amazon public service to get search results from these two services,  Then, it creates search result response containing all the items found from Amazon and eBay and return it.

ItemSearchTypeLibrary demonstrates the use of TypeLibrary.  It defines an ItemType. ItemSearchService uses ItemType to define its FindByKeywordsResponse.

ItemSearchErrorLibrary demonstrates the use of ErrorLibrary. The ErrorLibrary defined an ErrorData called "NoItemFound".  This ErrorData is used in ItemSearchServiceImpl to let client know no item is found for the given keyword.

To build and run the demo, In Eclipse, 

- install Turmeric Plugin;
- import item-search as maven project;
- clean build all the imported projects;
- start DemoAppServer under DemoApp project as Java application.  This starts up a jetty server;
- In a browser, open page http://localhost:8080/DemoApp/ItemSearch.html, type a keyword in the search field and press the search button. A table with search result will return.

