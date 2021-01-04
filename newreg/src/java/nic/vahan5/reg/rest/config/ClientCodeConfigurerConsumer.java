/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.config;

import org.springframework.http.codec.ClientCodecConfigurer;

/**
 * @author Kartikey Singh
 * Created this class to increase limit on max bytes to buffer
 * for a WebClient request. Default value is 256Kb, we set it to 512 Kb.
 * First encountered this when hitting localhost:8084/vahan/fee/save
 */
public class ClientCodeConfigurerConsumer implements java.util.function.Consumer<ClientCodecConfigurer> {

    @Override
    public void accept(ClientCodecConfigurer t) {
        t.defaultCodecs().maxInMemorySize(512*1024);
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
