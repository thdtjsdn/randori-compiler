package randori.annotations
{

/**
 * Indicates the kinds of program element to which an annotation type is applicable. 
 */
public final class Target extends Annotation
{
    public var value:Vector.<ElementType>;
    
    public function Target()
    {
    }
}
}