/* On utilise openapi-generator pour générer automatiquement une partie du code à partir de l'api.
La commande (utilisée dans ../) est :
java -jar openapi-generator-cli.jar generate -i openapi3.yaml -g scala-akka-http-server --additional-properties=useApachePekko=true -o pekko-server
*/

version := "1.0.0"
name := "api-presentation"
organization := "fr.mipn"
scalaVersion := "2.13.12"
val PekkoVersion = "1.0.1"
val PekkoHTTPVersion = "1.0.0"

resolvers += "Apache Public" at "https://repository.apache.org/content/groups/public"

/* Deux possibilités pour utiliser le code généré par openapi-generator : */

/* 1) soit on utilise l'option qui transforme les erreurs dues à d'anciennes syntaxes en warnings
https://docs.scala-lang.org/scala3/guides/migration/tooling-migration-mode.html 

Dans ce cas on utilisera scala 3 avec la syntaxe dans laquelle l'indentation est non significative, 
il faudra donc délimiter les blocs avec des accolades (on peut par contre utiliser la nouvelle
syntaxe if then, for do et while do). Cela se fait en décommentant la ligne suivante : */

// scalacOptions ++= Seq("-source:3.0-migration")

/* 2) Soit on demande à sbt de réécrire le code généré en deux temps de façon à utiliser l'indentation
significative. */
/* 2.1) on demande à sbt de réécrire les if for et while en if then for do, while do */

// décommenter, faire clean puis compile, recommenter
// scalacOptions ++= Seq("-new-syntax", "-rewrite")

/* 2.2) on demande ensuite à sbt de réécrire les accolades en indentations */

// décommenter, faire clean puis compile, recommenter 
// scalacOptions ++= Seq("-indent", "-rewrite")

/* Options toujours utiles */
//scalacOptions ++= Seq("-explain","-explain-types","-feature","-deprecation", "-language:implicitConversions")

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor-typed" % PekkoVersion,
  "org.apache.pekko" %% "pekko-stream" % PekkoVersion,
  "org.apache.pekko" %% "pekko-http" % PekkoHTTPVersion,
  "org.apache.pekko" %% "pekko-http-spray-json" % PekkoHTTPVersion,
  "com.typesafe.play" %% "play-json" % "2.9.2"
)
//.map(_.cross(CrossVersion.for3Use2_13))

libraryDependencies +=  "ch.qos.logback" % "logback-classic" % "1.2.3"


