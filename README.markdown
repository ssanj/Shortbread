Shortbread
----------

A simple plugin for running [QUnit](http://docs.jquery.com/Qunit) tests through [sbt](http://code.google.com/p/simple-build-tool/).

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
      //update the version to the current latest version
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

 By default QUnit test in `src/test/webapp/scripts/*.html` files are executed using the Firefox and Chrome drivers.
 You can override these defaults if you wish. See Configuration options.  

## Timeouts

To update page and script load timeouts create your custom drivers and add them to the driverSeq.  See [DefaultDrivers](Shortbread/blob/master/src/main/scala/DefaultDrivers.scala) for example configurations.
  
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

  The following are the configuration options and defaults defined for Shortbread. You can override any options you wish. Be sure to `reload` if you change any configuration options.

*Name of the scripts directory*
    def scriptDirectoryName = "scripts"

*Location of the test files* [default -> src/test/webapp/scripts]
    def testScriptPath: Path = sourceDirectoryName / testDirectoryName / webappDirectoryName / scriptDirectoryName

*Files that contain the QUnit tests*
    def scriptFiles = "*.html"

*The set of files that are executed* 
    def scriptFileSet: PathFinder = testScriptPath ** scriptFiles

*Should the browsers exit at the end of the tests?*
    def exitOnCompletion = true

*Browsers to use* [default -> Firefox and Chrome]
    def driverSeq:Seq[NamedDriver] = Seq(DefaultFoxConfig.webDriver, DefaultChromeConfig.webDriver)
    
#Configuring different browser combinations

 For custom driver configurations please see [Selenium Webdriver doco](http://seleniumhq.org/docs/09_webdriver.html#webdriver-implementations) and supply your custom driver to  the `driverSeq` option.
 See [DefaultDrivers](Shortbread/blob/master/src/main/scala/DefaultDrivers.scala) for example configurations.
 
#Selecting which browsers to run 
 
 If you have only some of the default browsers installed on your system, customize the `driverSeq` option to include
 only browsers you have.
 
 Eg. If you only have the Chrome browser installed on your system add this to your project file:
    override def driverSeq:Seq[NamedDriver] = Seq(DefaultChromeConfig.webDriver)
    
#Examples

Example use of the plugin can be found in: [Shortbread-Sample](http://github.com/ssanj/Shortbread-Sample)

