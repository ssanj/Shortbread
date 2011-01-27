Shortbread
----------

A simple plugin for running QUnit tests through [sbt](http://code.google.com/p/simple-build-tool/).

#Building from source

    git clone git@github.com:ssanj/Shortbread.git
    cd Shortbread
    sbt
    # From the sbt prompt:
    update #downloads any dependencies    
    publish-local #publishes artifact to your local repository


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
      //See documentation for a full list of options
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

#Examples

Example use of the plugin can be found in: [Shortbread-Sample](http://github.com/ssanj/Shortbread-Sample)

