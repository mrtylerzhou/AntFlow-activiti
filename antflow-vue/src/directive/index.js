import hasRole from './permission/hasRole'
import hasPermi from './permission/hasPermi'
import copyText from './common/copyText'
import colorSvgHover from './common/colorSvgHover'
import svgHover from './common/svgHover'

export default function directive(app){
  app.directive('hasRole', hasRole)
  app.directive('hasPermi', hasPermi)
  app.directive('copyText', copyText)
  app.directive('colorSvgHover', colorSvgHover)
  app.directive('svgHover', svgHover)
  app.directive('focus', {
    mounted(el) {
      el.focus();
    }
  })  
  app.directive('enter-number', { 
  }) 
}