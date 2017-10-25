package omar.wfs.app

import grails.transaction.Transactional
import groovy.json.JsonSlurper

import org.springframework.beans.factory.annotation.Value
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand

@Transactional( readOnly = true )
class GeoscriptClientService
{
  @Value('${omar.wfs.app.geoscript.url}')
  def geoscriptEndpoint

  @HystrixCommand(fallbackMethod = "serviceDown")
  def getCapabilitiesData()
  {
    def url = "${geoscriptEndpoint}/getCapabilitiesData".toURL()

    new JsonSlurper().parse( url )

  }

  String serviceDown() {
    return "Service is down"
  }

  @HystrixCommand(fallbackMethod = "serviceDown")
  def getSchemaInfoByTypeName(String typeName)
  {
    def url = "${geoscriptEndpoint}/getSchemaInfoByTypeName?typeName=${typeName}".toURL()

    new JsonSlurper().parse( url )
  }


  // The fallback method must match the same parameters of the method where you define the Hystrix Command
  String serviceDown(String typeName) {
    return "Service is down"
   }

  @HystrixCommand(fallbackMethod = "serviceDown")
  def queryLayer(String typeName, Map<String,Object> options, String resultType='results', String featureFormat=null)
  {
    def params = [
      typeName: typeName,
      resultType: resultType
    ]

    if ( options.max ) {
      params.max = options.max
    }

    if ( options.start ) {
      params.start = options.start
    }

    if ( options.filter ) {
      params.filter = options.filter
    }

    if ( featureFormat ) {
      params.featureFormat = featureFormat
    }

    if ( options.fields ) {
      params.fields = options.fields.join(',')
    }

    if ( options.sort ) {
      params.sort = options.sort.collect { it.join(' ') }.join(',')
    }

    println params

    def newParams = params.collect {
      "${it.key}=${URLEncoder.encode( it.value as String, 'UTF-8' )}"
    }.join('&')

    def url = "${geoscriptEndpoint}/queryLayer?${newParams}".toURL()

    // println url

    new JsonSlurper().parse( url )
  }


  // The fallback method must match the same parameters of the method where you define the Hystrix Command
  String serviceDown(String typeName, Map<String,Object> options, String resultType='results', String featureFormat=null) {
    return "Service is down"
  }
}
