Shortbread
----------

A simple plugin for running QUnit tests through [sbt](http://code.google.com/p/simple-build-tool/).

#Version

The lastest version of Shortbread is 0.0.11

#Building from source

    git clone git@github.com:ssanj/Shortbread.git
    cd Shortbread
    sbt
    update
    publish-local


#Declaring the plugin

In the project where you want to use Shortbread add it as a dependency to the plugin configuration found in 
`project/plugins/Plugins.scala`:

    import sbt._

    class Plugins(info:ProjectInfo) extends PluginDefinition(info) {
      val shortbread = "com.github.ssanj" % "shortbread" % "0.0.11"
    }


#Configuring a simple project

Mixin the `ShortbreadPlugin` trait into your project definition and customize options as needed:

    import sbt._
    import shortbread._
    import shortbread.DefaultDrivers._

    class Project(info:ProjectInfo) extends DefaultWebProject(info) with ShortbreadPlugin {
      //Use only the firefox driver. See Configuration options for a full list of options
      override def driverSeq:Seq[NamedDriver] = Seq(DefaultFoxConfig.webDriver)  
    }
  
#Default Configuration

 By default `src/test/webapp/scripts/*.html` files are executed using the drivers listed in your driverSeq method. 
 You can override these defaults if you wish. See Configuration options.
  
#Running Shortbread

Once you have updated both your Plugin and Project configurations:
    launch sbt #this should download the shortbread plugin from your local repo
    actions  #look for the 'shob' action
    shob #this will run the shortbread plugin

#Sample output

    > shob
    [info] 
    [info] == shob ==
    [info] Running scripts from: /home/sanj/projects/tools/SBT/Shortbread-Sample/src/test/webapp/scripts
    [info] Using Firefox driver >>>
    [info] 
    [info] Running -> file:///home/sanj/projects/tools/SBT/Shortbread-Sample/src/test/webapp/scripts/jquery-core-tests.html
    [info] total: 35, passed: 35, failed: 0
    [info] 
    [info] Running -> file:///home/sanj/projects/tools/SBT/Shortbread-Sample/src/test/webapp/scripts/qunit-website-example.html
    [error] Module -> Module B
    [error] TestCase -> some other test
    [error] Test -> fixed failing test
    [error] Expected: false, Received: true
    [error] Source -> ()@file:///home/sanj/projects/tools/SBT/Shortbread-Sample/src/test/webapp/scripts/qunit-website-example.html:32
    [info] total: 6, passed: 5, failed: 1
    [info] 
    [info] == shob ==
    [error] Error running shob: There were test failures

#Configuration options

  The following are the configuration options and defaults defined for Shortbread. You can override any options you wish.
  
    def scriptDirectoryName = "scripts"

    //location of the files exercising the javascript under test 
    def testScriptPath: Path = sourceDirectoryName / testDirectoryName / webappDirectoryName / scriptDirectoryName

    //files that contain the javascript tests
    def scriptFiles = "*.html"

    //the set of files that are executed 
    def scriptFileSet: PathFinder = testScriptPath ** scriptFiles

    //should the browsers exit at the end of the tests
    def exitOnCompletion = true

    //Browsers to use.
    def driverSeq:Seq[NamedDriver] = Seq(DefaultFoxConfig.webDriver, DefaultChromeConfig.webDriver)
    
#Configuring different browser combinations

 For custom driver configurations please see [Selenium Webdriver doco](http://seleniumhq.org/docs/09_webdriver.html#webdriver-implementations) and supply your custom driver to  the `driverSeq` option.
 See [DefaultDrivers](/blob/master/src/main/scala/DefaultDrivers.scala) for example configurations.
    
#Examples

Example use of the plugin can be found in: [Shortbread-Sample](http://github.com/ssanj/Shortbread-Sample)

