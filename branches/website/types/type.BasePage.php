<?php

class BasePage {

  private $title;
  private $charset;
  private $mimetype;
  private $meta;

  /**
   *
   * @param Body $body
   */
  public function generateBody(&$body) {
    $content = $this->getContent();
    if (empty($content))
      $body->add('<!-- no content -->');
    else if (is_array($content)) {
      foreach ($content as $element) {
        $body->add($element);
      }
    }
    else
      $body->add($content);
  }

  /**
   * @return HtmlElement
   */
  protected function getContent() {
    return false;
  }

  /**
   *
   * @param Head $head
   */
  public function generateHead(&$head) {
    // <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    $contentType;
    if (isset($this->meta['content-type']))
      $contentType = $this->meta['content-type'];
    else
      $contentType = $this->mimetype . '; charset=' . $this->charset;
    $metax = new HtmlElement('meta');
    $metax->setAttribute('http-equiv', 'content-type');
    $metax->setAttribute('content', $contentType);
    $head->add($metax);
    foreach ($this->meta as $key => $value) {
      if ($key == 'content-type')
        continue;
      $metax = new HtmlElement('meta');
      $metax->setAttribute('http-equiv', $key);
      $metax->setAttribute('content', $value);
      $head->add($metax);
    }
    if (!empty($this->title)) {
      $title = new HtmlElement('title');
      $title->add($this->title);
      $head->add($title);
    }
  }

  public static function create() {
    return new PageStart();
  }

  public function __construct() {
    $this->mimetype = 'text/html';
    $this->charset  = 'UTF-8';
    $this->meta = array();
  }

  public function __toString() {
    $page = new HtmlElement('html');
    $head = new Head();
    $this->generateHead($head);
    $page->add($head);
    $body = new Body();
    $this->generateBody($body);
    $page->add($body);
    //print_r($page);

    return $page->__toString();
  }

  public function getTitle() {
    return $this->title;
  }

  protected function setTitle($title) {
    $this->title = $title;
  }

  protected function setMetaData($key, $value) {
    $this->meta[$key] = $value;
  }

}

?>
