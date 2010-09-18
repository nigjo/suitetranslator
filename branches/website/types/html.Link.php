<?php
class Link extends HtmlElement{

  public function __construct() {
    parent::__construct('a');
  }

  public static function create($text, $url) {
    $link = new Link();
    $link->setAttribute('href', $url);
    $link->add($text);
    return $link;
  }

  public static function toPage($page, $title, $arguments=array()){
    $link = new Link();
    $href='?page='.$page;

    $link->setAttribute('href', $href);
    $link->add($title);
    return $link;
  }
  
}
?>
