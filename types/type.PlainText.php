<?php

class PlainText extends HtmlElement {

  private $text;

  public function __construct($text = '') {
    parent::__construct('');
    $this->text = $text;
  }

  public function __toString() {
    if(empty($this->text))
      return 'pt';
    return $this->text;
  }

  /**
   * @param PlainText $element 
   */
  public function add($element) {
    if($this->text==null)
      $this->text = '';
    $this->text.=(string) $element;
  }

}

?>
