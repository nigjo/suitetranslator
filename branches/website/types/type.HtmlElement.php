<?php

class HtmlElement {

  private $tag;
  private $content;
  private $attributes;

  public function __construct($tag, $contentText=false) {
    $this->tag = $tag;
    if ($contentText !== false) {
      $this->add(new PlainText($contentText));
    }
    $this->attributes = array();
  }

  public function __toString() {
    $text = '<' . $this->tag;
    foreach ($this->attributes as $key => $value) {
      $text.=' ' . $key . '="' . $value . '"';
    }
    if (empty($this->content))
      return $text . '/>';
    $text .= '>';
    foreach ($this->content as $element)
      $text.=$element;

    $text.='</' . $this->tag . '>';
    return $text;
  }

  /**
   * Fuegt ein neues Element diesem Element hinzu.
   *
   * @param HtmlElement $element
   */
  public function add($element) {
    if (is_string($element)) {
      $this->add(new PlainText($element));
      return;
    }
    if (empty($this->content))
      $this->content = array();
    $this->content[] = $element;
  }

  /**
   * setzt ein Attribut.
   *
   * @param string $key Schlüssel
   * @param string $value Wert
   */
  public function setAttribute($key, $value) {
    $this->attributes[$key] = $value;
  }

}

?>
