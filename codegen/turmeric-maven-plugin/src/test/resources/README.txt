Organization of this test resources tree is as follows ...

/src/test/resources/

  /legacy/
      Projects that conform to the legacy codegen approach of 
      utilizing the various project root properties files to configure
      the codegen tools. 
      These are projects that set the <legacy>true</legacy> parameter
      and no other parameters, allowing all configuration to arrive
      via various properties files, such as:
       * service_intf_project.properties 
       * service_metadata.properties 
       * service WSDL
       * service_impl_project.properties 
       * ServiceConfig.xml
       * ClientConfig.xml
       * and even ${project.properties} found in the pom.xml
      to name a few.
      
  /standard/
      Projects that conform to the maven project approach of utilizing
      the information present in the pom, and the project model to determine
      what the codegen configuration should be.
      These are projcts that DO NOT set the <legacy> parameter, opting
      to get their entire set of configuration options from mojo
      parameters.
  
  /hybrid/ 
      [FUTURE]
      Projects that have the legacy codegen properties files, but
      also have various maven project pom configuration values that
      should override the properties files values when managing
      codegen configurations.
  
