package com.portofrotterdam.parsers

import com.portofrotterdam.models._
import SegmentParser._
import fastparse.P
import fastparse.MultiLineWhitespace._

trait MessageParser {
  implicit def IFTSAIMessage[_ : P]: P[_] => P[IFTSAIMessage] = IFTSAIMessageParser.parser(_)
}

object IFTSAIMessageParser {
  private def SegmentGroup4Segment[_ : P]: P[SegmentGroup4] =
    P(TransportInformationSegment ~
      ReferenceSegment.rep(max = 9) ~
      FreeTextSegment.rep(max = 9) ~
      SegmentGroup5Segment.rep(max = 99)
    ).map(SegmentGroup4.tupled)

  private def SegmentGroup5Segment[_ : P]: P[SegmentGroup5] =
    P(LocationSegment ~
      DateTimePeriodSegment.rep(max = 9) ~
      ReferenceSegment.rep(max = 9) ~
      FreeTextSegment.rep(max = 9)
    ).map(SegmentGroup5.tupled)

  private def SegmentGroup6Segment[_ : P]: P[SegmentGroup6] =
    P(LocationSegment).map(SegmentGroup6.apply)

  def parser[_ : P]: P[IFTSAIMessage] =
    P(InterchangeHeaderSegment.? ~
      MessageHeaderSegment ~
      BeginningOfMessageSegment ~
      DateTimePeriodSegment.rep(max = 9) ~
      FreeTextSegment.rep(max = 99) ~
      SegmentGroup4Segment ~
      SegmentGroup6Segment.? ~
      MessageTrailerSegment
    ).map((IFTSAIMessage.apply _).tupled)

}
