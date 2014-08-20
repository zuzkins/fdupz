fdupz
=====

Duplicate files finder


How to run it for development
=====

The software is written in clojurescript and is expected to run under node-webkit. Follow these steps to setup your local development environment:

Installing the tools
---

1. you need to install jdk (pref 1.7+) for your platform
2. first install build tool called [Leiningen](https://github.com/technomancy/leiningen)
3. download appropriate build of [node-webkit](https://github.com/rogerwang/node-webkit) for your platform and unpack it 
4. you need npm to install dependencies for the app
   + install recent version of [nodejs](http://nodejs.org) (0.10.x recommended)
   + npm should already be part of the installation
   + `cd dir-where-fdupz-is`
   + `npm install`
   
all required tools should be now ready to run the dev environment

Running the dev environment
---

1. in terminal run `lein do cljsbuild clean, cljsbuild auto`
   + this will take its time for the first time it will run, and will automatically compile clojurescript down to javascript when any file changes
   + changes are then compiled incrementally
2. in other terminal window run command `lein repl`
   + this will start the interactive dev environment for clojure
3. after the repl starts
   + either in your favorite editor connect to nREPL or directly in the terminal
   + write `(go)` followed by ENTER in the REPL. This will fire up jetty server for serving the artefacts for the project
4. run the node-webkit
   + run the executable from the node-webkit you downloaded earlier
   + `cd dir-where-fdupz-is`
   + `$DIR_WITH_NODE_WEBKIT/nw/node-webkit.app/Contents/MacOS/node-webkit .`
       + if you have troubles running it on linux due to missing `libudev.so.0` try one to run it as described on the [wiki](https://github.com/rogerwang/node-webkit/wiki/The-solution-of-lacking-libudev.so.0)
       + the `.` at the end is important
       + the path will depend on where you unzipped the node-webkit distribution and the runnable file depends on the platform you are trying to run it on
       
5. node-webkit should fire up and fetch the application automatically
   
