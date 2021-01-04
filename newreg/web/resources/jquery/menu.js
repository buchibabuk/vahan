//var jqs =   $.noConflict();
//
//jQuery.fn.initMenu = function() {  
//    return this.each(function(){
//        var theMenu = jqs(this).get(0);
//        jqs('.accordionContent', this).hide();
//        jqs('li.expand > .accordionContent', this).show();
//        jqs('li.expand > .accordionContent', this).prev().addClass('active');
//        jqs('li a', this).click(
//            function(e) {
//                e.stopImmediatePropagation();
//                var theElement = jqs(this).next();
//                var parent = this.parentNode.parentNode;
//                if(jqs(parent).hasClass('noaccordion')) {
//                    if(theElement[0] === undefined) {
//                        window.location.href = this.href;
//                    }
//                    jqs(theElement).slideToggle('normal', function() {
//                        if (jqs(this).is(':visible')) {
//                            jqs(this).prev().addClass('active');
//                        }
//                        else {
//                            jqs(this).prev().removeClass('active');
//                        }    
//                    });
//                    return false;
//                }
//                else {
//                    if(theElement.hasClass('accordionContent') && theElement.is(':visible')) {
//                        if(jqs(parent).hasClass('collapsible')) {
//                            jqs('.accordionContent:visible', parent).first().slideUp('normal',
//                            function() {
//                                jqs(this).prev().removeClass('active');
//                            }
//                        );
//                        return false;  
//                    }
//                    return false;
//                }
//                if(theElement.hasClass('accordionContent') && !theElement.is(':visible')) {         
//                    jqs('.accordionContent:visible', parent).first().slideUp('normal', function() {
//                        jqs(this).prev().removeClass('active');
//                    });
//                    theElement.slideDown('normal', function() {
//                        jqs(this).prev().addClass('active');
//                    });
//                    return false;
//                }
//            }
//        }
//    );
//});
//};
//
//jqs(document).ready(function() {jqs('.accordion ul').initMenu();});