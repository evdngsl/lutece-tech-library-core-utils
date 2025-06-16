package fr.paris.lutece.util.http;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import fr.paris.lutece.portal.web.ServletLocalVariables;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class AppPathUtil
{

    public static final String SESSION_BASE_URL = "base_url";
    private static final int PORT_NUMBER_HTTP = 80;
    private static final String PROPERTY_BASE_URL = "lutece.base.url";
    private static final String PROPERTY_VIRTUAL_HOST_KEY_PARAMETER = "virtualHostKey.parameterName";
    private static final String PROPERTY_VIRTUAL_HOST = "virtualHost.";
    private static final String SUFFIX_BASE_URL = ".baseUrl";
    private static final String SLASH = "/";
    private static final String DOUBLE_POINTS = ":";
    private static final Config _config = ConfigProvider.getConfig( );
    
    private AppPathUtil( )
    {
    }

    /**
     * Return the url of the webapp, built from the request
     *
     * @param request
     *            The HttpServletRequest
     * @return strBase the webapp url
     */
    public static String getBaseUrl( HttpServletRequest request )
    {
        if ( request == null )
        {
            return getBaseUrl( );
        }

        String strBase;

        // Search for a Virtual Host Base Url defined in the request
        strBase = getVirtualHostBaseUrl( request );

        // If not found, get the base url from session
        if ( ( strBase == null ) || strBase.equals( StringUtils.EMPTY ) )
        {
            HttpSession session = request.getSession( false );

            if ( session != null )
            {
                Object oBase = session.getAttribute( SESSION_BASE_URL );

                if ( oBase != null )
                {
                    strBase = (String) oBase;
                }
            }
        }

        // If not found, get the base url from the config.properties
        if ( ( strBase == null ) || ( strBase.equals( StringUtils.EMPTY ) ) )
        {
            strBase = _config.getOptionalValue( PROPERTY_BASE_URL, String.class ).orElse( null ); 
        }

        if ( ( strBase == null ) || ( strBase.equals( StringUtils.EMPTY ) ) )
        {
            // Dynamic base URL if not defined in the properties
            strBase = request.getScheme( ) + DOUBLE_POINTS + SLASH + SLASH + request.getServerName( );

            int nPort = request.getServerPort( );

            if ( nPort != PORT_NUMBER_HTTP )
            {
                strBase += ( DOUBLE_POINTS + nPort );
            }

            strBase += request.getContextPath( );
        }

        if ( !strBase.endsWith( SLASH ) )
        {
            strBase += SLASH;
        }

        return strBase;
    }
    
    private static String getBaseUrl( )
    {
        HttpServletRequest request = ServletLocalVariables.getRequest( );

        if ( request != null )
        {
            return getBaseUrl( request );
        }

        String strBaseUrl = _config.getOptionalValue( PROPERTY_BASE_URL, String.class ).orElse( null );

        if ( strBaseUrl == null )
        {
            strBaseUrl = StringUtils.EMPTY;
        }
        else
        {
            if ( !strBaseUrl.endsWith( SLASH ) )
            {
                strBaseUrl += SLASH;
            }
        }

        return strBaseUrl;
    }
    
    /**
     * Gets a Base Url for a virtual host if the request contains a virtual host key
     *
     * @param request
     *            The HTTP request
     * @return A virtual host base url if present, otherwise null.
     */
    private static String getVirtualHostBaseUrl( HttpServletRequest request )
    {
        String strBaseUrl = null;
        String strVirtalHostKey = getVirtualHostKey( request );

        if ( ( strVirtalHostKey != null ) && ( !strVirtalHostKey.equals( "" ) ) )
        {
            // If found gets the Base url for this virtual host by its key
            strBaseUrl = _config.getOptionalValue( PROPERTY_VIRTUAL_HOST + strVirtalHostKey + SUFFIX_BASE_URL, String.class ).orElse( null );
        }

        return strBaseUrl;
    }
    
    /**
     * Gets a Virtual Host Key if the request contains a virtual host key
     *
     * @param request
     *            The HTTP request
     * @return A Virtual Host Key if present, otherwise null.
     */
    public static String getVirtualHostKey( HttpServletRequest request )
    {
        String strVirtalHostKey = null;

        // Get from config.properties the parameter name for virtual host keys
        String strVirtualHostKeyParameter = _config.getOptionalValue( PROPERTY_VIRTUAL_HOST_KEY_PARAMETER, String.class ).orElse( null );

        if ( ( request != null ) && ( strVirtualHostKeyParameter != null ) && ( !strVirtualHostKeyParameter.equals( "" ) ) )
        {
            // Search for this parameter into the request
            strVirtalHostKey = request.getParameter( strVirtualHostKeyParameter );
        }

        return strVirtalHostKey;
    }
}
