<?php

class PageStart extends BasePage {
  public function __construct() {
    parent::__construct();
    $this->setTitle('NetBeans Suite Translator');
  }

  protected function getContent(){
    $tags = array();
    $tags[]=new HtmlElement('h1', 'NetBeans Suite Translator');
    $tags[]=new Text('comming soon...');
    $tags[]=new HtmlElement('hr');
    $tags[]=Link::create(
      'Project Site',
      'http://kenai.com/projects/suitetranslator');
    return $tags;
  }
}

?>
