package fr.paris.lutece.util.xml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlMarshaller
{

    private static final XmlMapper xmlMapper = new XmlMapper( );

    private XmlMarshaller( )
    {

    }

    /**
     * Deserialize an XML string into an object of the specified type.
     *
     * @param requestBody
     *            the XML string to deserialize
     * @param targetType
     *            the class of the object to deserialize into
     * @param <T>
     *            the type of the object
     * @return the deserialized object
     * @throws JsonProcessingException
     *             if there is an error during deserialization
     */
    public static <T> T deserialize( String requestBody, Class<T> targetType ) throws JsonProcessingException
    {
        // Deserialize the XML string into the target type
        return xmlMapper.readValue( requestBody, targetType );
    }

    /**
     * Serialize an object to an XML string.
     *
     * @param objectValue
     *            The object to serialize
     * @return The XML string representation of the object
     * @throws JsonProcessingException
     *             if there is an error during serialization
     */
    public static String serialize( Object objectValue ) throws JsonProcessingException
    {
        // serialize to XML string
        return xmlMapper.writeValueAsString( objectValue );
    }

}
