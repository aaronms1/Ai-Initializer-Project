//package org.dacss.projectinitai.services;
//
//import com.vaadin.flow.server.auth.AnonymousAllowed;
//import com.vaadin.hilla.BrowserCallable;
//import org.dacss.projectinitai.tar.TarActions;
//import org.dacss.projectinitai.tar.TarIface;
//import org.dacss.projectinitai.tar.TarCompressorUtil;
//import org.dacss.projectinitai.tar.TarDestroyUtil;
//import org.dacss.projectinitai.tar.TarExtractorUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
///**
// * <h1>{@link TarService}</h1>
// * Service class for tar operations.
// */
//@Service
////@Endpoint
//@BrowserCallable
//@AnonymousAllowed
//public class TarService implements TarIface {
//
//    private static final Logger log = LoggerFactory.getLogger(TarService.class);
//    private static final String RED = "\u001B[31m";
//    private static final String GREEN = "\u001B[32m";
//    private static final String RESET = "\u001B[0m";
//
//    public TarService() {}
//
//    @Override
//    public Object processTar(TarActions action) {
//        Object result;
//        try {
//            result = switch (action) {
//                case COMPRESS -> TarCompressorUtil.createTarFile();
//                case EXTRACT -> TarExtractorUtil.extractTarFile();
//                case DESTROY -> TarDestroyUtil.destroyTarFile();
//            };
//        } catch (Exception tarExc) {
//            log.error(RED + "Error handling tar operation: {}" + RESET, action, tarExc);
//            return "Error handling tar operation: " + action;
//        } finally {
//            log.info(GREEN + "TarService action completed: {}" + RESET, action);
//        }
//        return result;
//    }
//}
