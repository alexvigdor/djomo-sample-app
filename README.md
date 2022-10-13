# djomo-sample-app
A sample JAX-RS micro service using djomo

This is a simple service that demonstrates baseline best practices using djomo with JAX-RS.  It offers basic REST endpoints for storing and retrieving "Widgets", a mock domain model.

Rather than being completely bare-bones, this sample tries to be realistic; it registers a DAO for widgets using a JAX-RS ContextResolver, and uses bean validation annotations to check the model integrity before storage.  It also customizes the djomo Json with a JsonResolver that injects the DAO from JAX-RS providers into the Json for use in custom filters.

Widgets are identified by UUID and may contain a list of UUIDs linking to other related widgets.  A second set of endpoints allows REST callers to store and retrieve multiple widgets at once using a nested Widget model in place of the UUID reference; these endpoints use custom djomo filters to deconstruct and reconstruct the nested widget model from the underlying DAO dynamically during JSON processing.

This sample demonstrates how djomo opens up possibilities beyond typical JAX-RS patterns; instead of having actual java classes representing the nested widget model, that must be loaded fully into memory before storing or serializing the nested objects, the parser filter stores each nested object as soon as it is parsed and holds only the UUID in memory, while the visitor filter loads each nested widget just in time to serialize it, allowing more efficient use of resources, at the expense of some additional complexity in development and troubleshooting.

The included unit test demonstrates using Jersey's JAX-RS test support framework to provide test coverage of the JAX-RS endpoints, and the application can also be run for manual testing using the jetty plugin:

`mvn jetty:run`