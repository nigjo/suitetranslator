<?php
/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Text
 *
 * @author nigjo
 */
class Text extends HtmlElement{
  public function __construct($contentText = false) {
    parent::__construct('p', $contentText);
  }
}
?>
