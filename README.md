Prerequisites:
 - Have a JDK installed
 - Have an instance of Arango accessible without credentials running locally.
 - Have a database called "testdb"
 - Have a collection called "element"

Run the project with:
`./gradlew run`

The project will create a document in the Arango collection and then retrieve it using an AQL query.

The document has a field (`attributes > int_attr_0 > value`) which is an integer (BigDecimal in the Java model), the project will 
check that the document that is read back has the same integer value (e.g. no decimal places) and print the results to stdout.

If you change the `insertFixture` variable in `Main.java` to false and then edit the document in the Arango web ui before running the project
again then it will pass.